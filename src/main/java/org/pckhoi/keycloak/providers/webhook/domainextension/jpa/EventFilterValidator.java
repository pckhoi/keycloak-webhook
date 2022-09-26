package org.pckhoi.keycloak.providers.webhook.domainextension.jpa;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.keycloak.events.EventType;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

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
