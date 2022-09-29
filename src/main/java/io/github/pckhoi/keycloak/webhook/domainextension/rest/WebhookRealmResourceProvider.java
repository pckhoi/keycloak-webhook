// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.webhook.domainextension.rest;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class WebhookRealmResourceProvider implements RealmResourceProvider {

    private KeycloakSession session;

    public WebhookRealmResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new WebhookRestResource(session);
    }

    @Override
    public void close() {
    }

}
