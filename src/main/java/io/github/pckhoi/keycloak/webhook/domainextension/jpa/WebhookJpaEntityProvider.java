// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.webhook.domainextension.jpa;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.Arrays;
import java.util.List;

public class WebhookJpaEntityProvider implements JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        return Arrays.asList(Webhook.class, EventFilter.class);
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/webhook-changelog.xml";
    }

    @Override
    public void close() {
    }

    @Override
    public String getFactoryId() {
        return WebhookJpaEntityProviderFactory.ID;
    }
}
