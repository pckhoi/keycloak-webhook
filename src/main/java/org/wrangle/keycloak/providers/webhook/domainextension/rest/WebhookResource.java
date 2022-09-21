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

import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;
import org.wrangle.keycloak.providers.webhook.domainextension.WebhookRepresentation;
import org.wrangle.keycloak.providers.webhook.domainextension.spi.WebhookService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class WebhookResource {

    private final KeycloakSession session;

    public WebhookResource(KeycloakSession session) {
        this.session = session;
    }

    @GET
    @Path("")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public List<WebhookRepresentation> getWebhooks() {
        return session.getProvider(WebhookService.class).listWebhooks();
    }

    @DELETE
    @Path("")
    @NoCache
    public void deleteAllWebhooks() {
        session.getProvider(WebhookService.class).deleteAllWebhooks();
    }

    @POST
    @Path("")
    @NoCache
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createWebhook(WebhookRepresentation rep) {
        session.getProvider(WebhookService.class).addWebhook(rep);
        return Response.created(session.getContext().getUri().getAbsolutePathBuilder().path(rep.getId()).build())
                .build();
    }

    @GET
    @NoCache
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public WebhookRepresentation getWebhook(@PathParam("id") final String id) {
        return session.getProvider(WebhookService.class).findWebhook(id);
    }

}