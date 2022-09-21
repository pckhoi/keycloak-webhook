package org.wrangle.keycloak.providers.webhook.events;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

public class WebhookEventListenerProvider implements EventListenerProvider {

    public WebhookEventListenerProvider(KeycloakSession session) {
    }

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
    }

    @Override
    public void close() {
    }
}