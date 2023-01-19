// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.webhook.domainextension.spi.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import io.github.pckhoi.keycloak.webhook.domainextension.spi.WebhookService;
import io.github.pckhoi.keycloak.webhook.domainextension.spi.WebhookServiceProviderFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

public class WebhookServiceProviderFactoryImpl
        implements WebhookServiceProviderFactory, ServerInfoAwareProviderFactory {

    @Override
    public WebhookService create(KeycloakSession session) {
        return new WebhookServiceImpl(session);
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
    public String getId() {
        return "webhookServiceImpl";
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        Map<String, String> ret = new LinkedHashMap<>();
        ret.put("version", "15.1.1.1");
        return ret;
    }

}
