// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.webhook.domainextension.spi.impl;

import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import io.github.pckhoi.keycloak.webhook.domainextension.EventFilterRepresentation;
import io.github.pckhoi.keycloak.webhook.domainextension.WebhookRepresentation;
import io.github.pckhoi.keycloak.webhook.domainextension.jpa.EventFilter;
import io.github.pckhoi.keycloak.webhook.domainextension.jpa.Webhook;
import io.github.pckhoi.keycloak.webhook.domainextension.spi.WebhookService;

import javax.persistence.EntityManager;
import java.util.LinkedList;
import java.util.List;

public class WebhookServiceImpl implements WebhookService {

    private final KeycloakSession session;

    public WebhookServiceImpl(KeycloakSession session) {
        this.session = session;
        if (getRealm() == null) {
            throw new IllegalStateException("The service cannot accept a session without a realm in it's context.");
        }
    }

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    protected RealmModel getRealm() {
        return session.getContext().getRealm();
    }

    @Override
    public List<WebhookRepresentation> listWebhooks() {
        List<Webhook> webhookEntities = getEntityManager().createNamedQuery("findByRealm", Webhook.class)
                .setParameter("realmId", getRealm().getId())
                .getResultList();

        List<WebhookRepresentation> result = new LinkedList<>();
        for (Webhook entity : webhookEntities) {
            result.add(new WebhookRepresentation(entity));
        }
        return result;
    }

    @Override
    public WebhookRepresentation findWebhook(String id) {
        EntityManager em = getEntityManager();
        Webhook entity = em.find(Webhook.class, id);

        if (entity == null) {
            return null;
        }

        List<EventFilter> filterEntities = em.createNamedQuery("findByWebhook", EventFilter.class)
                .setParameter("webhookId", entity.getId())
                .getResultList();
        entity.setFilters(filterEntities);
        return new WebhookRepresentation(entity);
    }

    @Override
    public WebhookRepresentation addWebhook(WebhookRepresentation webhook) {
        EntityManager em = getEntityManager();
        String webhookId = KeycloakModelUtils.generateId();

        try {
            em.getTransaction().begin();

            Webhook webhookEntity = new Webhook();
            webhookEntity.setId(webhookId);
            webhookEntity.setName(webhook.getName());
            webhookEntity.setRealmId(getRealm().getId());
            webhookEntity.setURL(webhook.getURL());
            em.persist(webhookEntity);

            for (EventFilterRepresentation filter : webhook.getFilters()) {
                EventFilter filterEntity = new EventFilter();
                String filterId = KeycloakModelUtils.generateId();
                filterEntity.setId(filterId);
                filterEntity.setUserEventType(filter.getUserEventType());
                filterEntity.setAdminEventOperationType(filter.getAdminEventOperationType());
                filterEntity.setAdminEventResourceType(filter.getAdminEventResourceType());
                filterEntity.setWebhook(webhookEntity);
                em.persist(filterEntity);
                filter.setId(filterId);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }

        webhook.setId(webhookId);
        return webhook;
    }

    @Override
    public void deleteAllWebhooks() {
        EntityManager em = getEntityManager();
        List<Webhook> webhookEntities = em.createNamedQuery("findByRealm", Webhook.class)
                .setParameter("realmId", getRealm().getId())
                .getResultList();

        for (Webhook entity : webhookEntities) {
            em.remove(entity);
        }
    }

    @Override
    public void deleteWebhook(String id) {
        EntityManager em = getEntityManager();
        em.remove(em.find(Webhook.class, id));
    }

    public void close() {
        // Nothing to do.
    }

}
