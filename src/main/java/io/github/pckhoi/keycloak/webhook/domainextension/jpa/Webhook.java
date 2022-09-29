// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.webhook.domainextension.jpa;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Webhook entity defines the following named query
 * 
 * <ul>
 * <li><b>findById</b>: find webhook by
 * <code>id</code></li>
 * <li><b>findByRealm</b>: find all webhooks with
 * <code>realmId</code></li>
 * <li><b>findByRealmUserEventType</b>: find all webhooks
 * with <code>realmId</code> and
 * <code>userEventType</code> equal param of the same name</li>
 * <li><b>findByRealmAdminEvent</b>: find all webhooks with
 * <code>realmId</code> and either matching <code>adminEventResourceType</code>,
 * or both <code>adminEventResourceType</code> and
 * <code>adminEventResourceType</code> matching</li>
 * </ul>
 */
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
