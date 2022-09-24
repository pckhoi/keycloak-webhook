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

package org.wrangle.keycloak.providers.webhook.domainextension.rest;

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

    private void checkRealmAdmin() {
        if (auth == null) {
            throw new NotAuthorizedException("Bearer");
        } else if (auth.getToken().getRealmAccess() == null
                || !auth.getToken().getRealmAccess().isUserInRole("admin")) {
            throw new ForbiddenException("Does not have realm admin role");
        }
    }

}
