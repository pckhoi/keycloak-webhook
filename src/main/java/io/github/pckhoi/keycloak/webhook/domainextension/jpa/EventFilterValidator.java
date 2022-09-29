package io.github.pckhoi.keycloak.webhook.domainextension.jpa;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.keycloak.events.EventType;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

/**
 * EventFilterValidator makes sure that:
 * 
 * <ul>
 * <li><code>userEventType</code> and <code>adminEventResourceType</code> cannot
 * be both defined</li>
 * <li><code>userEventType</code> and <code>adminEventOperationType</code>
 * cannot be both defined</li>
 * <li>either <code>userEventType</code> or <code>adminEventResourceType</code>
 * must be defined</li>
 * </ul>
 */
public class EventFilterValidator implements ConstraintValidator<EventFilterAnnotation, EventFilter> {
    public void initialize(EventFilterAnnotation annotation) {
    }

    public boolean isValid(EventFilter object, ConstraintValidatorContext context) {
        if (!(object instanceof EventFilter)) {
            throw new IllegalArgumentException("@EventFilterAnnotation only applies to EventFilter objects");
        }
        EventFilter filter = (EventFilter) object;
        EventType uet = filter.getUserEventType();
        OperationType ot = filter.getAdminEventOperationType();
        ResourceType rt = filter.getAdminEventResourceType();
        if (uet != null) {
            if (rt != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "filter cannot define both userEventType and adminEventResourceType");
                return false;
            }
            if (ot != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "filter cannot define both userEventType and adminEventOperationType");
                return false;
            }
        }
        if (rt == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "filter must define either userEventType or adminEventResourceType");
            return false;
        }
        return true;
    }
}
