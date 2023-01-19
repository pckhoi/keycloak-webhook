// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.webhook.domainextension.jpa;

import java.util.LinkedHashMap;
import java.util.Map;

import org.keycloak.Config.Scope;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

public class WebhookJpaEntityProviderFactory implements JpaEntityProviderFactory, ServerInfoAwareProviderFactory {

    protected static final String ID = "pckhoi-webhook";

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

    @Override
    public Map<String, String> getOperationalInfo() {
        Map<String, String> ret = new LinkedHashMap<>();
        ret.put("version", "15.1.1.1");
        return ret;
    }

}
