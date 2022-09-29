// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.webhook.domainextension.rest;

import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;
import io.github.pckhoi.keycloak.webhook.domainextension.WebhookRepresentation;
import io.github.pckhoi.keycloak.webhook.domainextension.spi.WebhookService;

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

    @DELETE
    @Path("{id}")
    @NoCache
    public void deleteWebhook(@PathParam("id") final String id) {
        session.getProvider(WebhookService.class).deleteWebhook(id);
    }

}