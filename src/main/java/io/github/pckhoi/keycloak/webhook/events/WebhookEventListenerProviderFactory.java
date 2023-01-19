// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.webhook.events;

import java.util.LinkedHashMap;
import java.util.Map;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

public class WebhookEventListenerProviderFactory
        implements EventListenerProviderFactory, ServerInfoAwareProviderFactory {

    public static final String PROVIDER_ID = "pckhoi-webhook";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new WebhookEventListenerProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        Map<String, String> ret = new LinkedHashMap<>();
        ret.put("version", "15.1.1.1");
        return ret;
    }
}
