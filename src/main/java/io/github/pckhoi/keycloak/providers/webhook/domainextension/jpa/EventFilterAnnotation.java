// SPDX-License-Identifier: Apache-2.0

package io.github.pckhoi.keycloak.providers.webhook.domainextension.jpa;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = EventFilterValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventFilterAnnotation {
    String message() default "invalid event filter";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
