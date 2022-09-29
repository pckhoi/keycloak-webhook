// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.providers.webhook.domainextension.jpa;

import org.keycloak.Config.Scope;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class WebhookJpaEntityProviderFactory implements JpaEntityProviderFactory {

    protected static final String ID = "webhook-entity-provider";

    @Override
    public JpaEntityProvider create(KeycloakSession session) {
        return new WebhookJpaEntityProvider();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void init(Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

}
