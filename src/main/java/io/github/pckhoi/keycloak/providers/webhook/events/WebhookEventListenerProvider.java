// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.providers.webhook.events;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.jboss.logging.Logger;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import io.github.pckhoi.keycloak.providers.webhook.domainextension.jpa.Webhook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebhookEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;
    private static final Logger logger = Logger.getLogger(WebhookEventListenerProvider.class);
    private ExecutorService executorService;

    public WebhookEventListenerProvider(KeycloakSession session) {
        this.session = session;
        if (getRealm() == null) {
            throw new IllegalStateException(
                    "The event listener cannot accept a session without a realm in it's context.");
        }
        executorService = Executors.newCachedThreadPool();
    }

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    protected RealmModel getRealm() {
        return session.getContext().getRealm();
    }

    private void hitWebhooks(List<Webhook> webhookEntities, String jsonPayload) {
        for (Webhook entity : webhookEntities) {
            Runnable task = () -> {
                try {
                    URL url = new URL(entity.getURL());
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    con.setConnectTimeout(5000);
                    con.setReadTimeout(5000);
                    con.setInstanceFollowRedirects(false);
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
                    out.writeBytes(jsonPayload);
                    out.flush();
                    out.close();
                    int responseCode = con.getResponseCode();
                    logger.debugv("response status {0} from {1}", responseCode, entity.getURL());
                } catch (IOException e) {
                    logger.errorv("IOException: {0}", e);
                }
            };
            executorService.execute(task);
        }
    }

    @Override
    public void onEvent(Event event) {
        EntityManager em = getEntityManager();
        List<Webhook> webhookEntities = em.createNamedQuery("findByRealmUserEventType", Webhook.class)
                .setParameter("realmId", getRealm().getId())
                .setParameter("userEventType", event.getType())
                .getResultList();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonResult = mapper.writeValueAsString(new UserEventRepresentation(event));
            this.hitWebhooks(webhookEntities, jsonResult);
        } catch (JsonProcessingException e1) {
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        EntityManager em = getEntityManager();
        List<Webhook> webhookEntities = em.createNamedQuery("findByRealmAdminEvent", Webhook.class)
                .setParameter("realmId", getRealm().getId())
                .setParameter("adminEventOperationType", event.getOperationType())
                .setParameter("adminEventResourceType", event.getResourceType())
                .getResultList();
        if (webhookEntities.size() == 0) {
            return;
        }
        logger.debugv("{0} {1}: found {2} matching webhooks", event.getOperationType(), event.getResourceType(),
                webhookEntities.size());
        ObjectMapper mapper = new ObjectMapper();
        try {
            AdminEventRepresentation repr = new AdminEventRepresentation(event);
            String jsonResult = mapper.writeValueAsString(repr);
            this.hitWebhooks(webhookEntities, jsonResult);
        } catch (JsonProcessingException e) {
            logger.errorv("error processing json: {0}", e);
        }
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}