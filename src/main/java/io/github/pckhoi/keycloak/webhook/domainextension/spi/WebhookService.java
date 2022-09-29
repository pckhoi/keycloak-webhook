// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.webhook.domainextension.spi;

import org.keycloak.provider.Provider;
import io.github.pckhoi.keycloak.webhook.domainextension.WebhookRepresentation;

import java.util.List;

public interface WebhookService extends Provider {

    List<WebhookRepresentation> listWebhooks();

    WebhookRepresentation findWebhook(String id);

    WebhookRepresentation addWebhook(WebhookRepresentation webhook);

    void deleteAllWebhooks();

    void deleteWebhook(String id);
}
