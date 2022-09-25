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

package org.pckhoi.keycloak.providers.webhook.domainextension.jpa;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "WEBHOOK")
@NamedQueries({
        @NamedQuery(name = "findById", query = "select w " + "from Webhook w "
                + "where w.id = :id"),
        @NamedQuery(name = "findByRealm", query = "select w " + "from Webhook w "
                + "where w.realmId = :realmId"),
        @NamedQuery(name = "findByRealmUserEventType", query = "select distinct w "
                + "from Webhook w "
                + "inner join w.filters as f "
                + "where w.realmId = :realmId " + "and f.userEventType = :userEventType"),
        @NamedQuery(name = "findByRealmAdminEvent", query = "select distinct w "
                + "from Webhook w "
                + "inner join w.filters as f "
                + "where w.realmId = :realmId "
                + "and ((f.adminEventOperationType = :adminEventOperationType and f.adminEventResourceType = :adminEventResourceType) "
                + "or (f.adminEventOperationType = :adminEventOperationType and f.adminEventResourceType is null) "
                + "or (f.adminEventOperationType is null and f.adminEventResourceType = :adminEventResourceType))")
})
public class Webhook {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "REALM_ID", nullable = false)
    private String realmId;

    @Column(name = "URL", nullable = false)
    private String url;

    @OneToMany(mappedBy = "webhook")
    private List<EventFilter> filters;

    public String getId() {
        return id;
    }

    public String getRealmId() {
        return realmId;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return url;
    }

    public List<EventFilter> getFilters() {
        return filters;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public void setFilters(List<EventFilter> filters) {
        this.filters = filters;
    }
}
