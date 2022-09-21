/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wrangle.keycloak.providers.webhook.domainextension.spi.impl;

import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.wrangle.keycloak.providers.webhook.domainextension.WebhookRepresentation;
import org.wrangle.keycloak.providers.webhook.domainextension.jpa.Webhook;
import org.wrangle.keycloak.providers.webhook.domainextension.spi.WebhookService;

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
        Webhook entity = getEntityManager().find(Webhook.class, id);
        return entity == null ? null : new WebhookRepresentation(entity);
    }

    @Override
    public WebhookRepresentation addWebhook(WebhookRepresentation webhook) {
        Webhook entity = new Webhook();
        String id = webhook.getId() == null ? KeycloakModelUtils.generateId() : webhook.getId();
        entity.setId(id);
        entity.setName(webhook.getName());
        entity.setRealmId(getRealm().getId());
        getEntityManager().persist(entity);

        webhook.setId(id);
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

    public void close() {
        // Nothing to do.
    }

}
