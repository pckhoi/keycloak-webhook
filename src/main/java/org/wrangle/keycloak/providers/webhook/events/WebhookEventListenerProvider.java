package org.wrangle.keycloak.providers.webhook.events;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.wrangle.keycloak.providers.webhook.domainextension.jpa.Webhook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebhookEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;
    private OkHttpClient client;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public WebhookEventListenerProvider(KeycloakSession session) {
        this.session = session;
        if (getRealm() == null) {
            throw new IllegalStateException(
                    "The event listener cannot accept a session without a realm in it's context.");
        }
        this.client = new OkHttpClient();
    }

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    protected RealmModel getRealm() {
        return session.getContext().getRealm();
    }

    private void hitWebhooks(List<Webhook> webhookEntities, String jsonPayload) {
        for (Webhook entity : webhookEntities) {
            RequestBody body = RequestBody.create(JSON, jsonPayload);
            Request request = new Request.Builder()
                    .url(entity.getURL())
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                }
            });
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
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonResult = mapper.writeValueAsString(new AdminEventRepresentation(event));
            this.hitWebhooks(webhookEntities, jsonResult);
        } catch (JsonProcessingException e1) {
        }
    }

    @Override
    public void close() {
    }
}