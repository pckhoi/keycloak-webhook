// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.webhook.domainextension.rest;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;

public class WebhookRestResource {

    private final KeycloakSession session;
    private final AuthenticationManager.AuthResult auth;

    public WebhookRestResource(KeycloakSession session) {
        this.session = session;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
    }

    @Path("webhooks")
    public WebhookResource getWebhookResource() {
        checkRealmAdmin();
        return new WebhookResource(session);
    }

    /**
     * checkRealmAdmin ensures that only users with "realm-management.realm-admin"
     * role can access this resource
     */
    private void checkRealmAdmin() {
        if (auth == null) {
            throw new NotAuthorizedException("Bearer");
        } else if (auth.getToken().getRealmAccess() == null
                || !auth.getToken().getResourceAccess().get("realm-management").isUserInRole("realm-admin")) {
            throw new ForbiddenException("Does not have realm-management.realm-admin role");
        }
    }

}
