# syntax=docker/dockerfile:1

ARG KEYCLOAK_VERSION=15.1.1
ARG PROVIDERS_VERSION=1

FROM maven:3.6.0-jdk-11-slim AS providers

ARG KEYCLOAK_VERSION
ARG PROVIDERS_VERSION

RUN mkdir /home/.m2
WORKDIR /home/.m2

COPY pom.xml /home/app/pom.xml
COPY src /home/app/src

RUN --mount=type=cache,target=/root/.m2 \
    cd /home/app \
    && mvn versions:set -DnewVersion=$KEYCLOAK_VERSION.$PROVIDERS_VERSION -Dkeycloak.version=$KEYCLOAK_VERSION \
    && mvn clean package -Dkeycloak.version=$KEYCLOAK_VERSION

FROM quay.io/keycloak/keycloak:${KEYCLOAK_VERSION} AS e2e_test

ARG KEYCLOAK_VERSION
ARG PROVIDERS_VERSION
ARG KC_HOME_DIR=/opt/jboss/keycloak





# COPY --from=providers \
#     /home/app/target/webhook-$KEYCLOAK_VERSION.$PROVIDERS_VERSION.jar \
#     ${KC_HOME_DIR}/standalone/deployments



COPY module.xml ${KC_HOME_DIR}/modules/io/github/pckhoi/keycloak/webhook/main/

COPY --from=providers \
    /home/app/target/webhook-$KEYCLOAK_VERSION.$PROVIDERS_VERSION.jar \
    ${KC_HOME_DIR}/modules/io/github/pckhoi/keycloak/webhook/main/



# COPY --from=providers \
#     /home/app/target/webhook-$KEYCLOAK_VERSION.$PROVIDERS_VERSION.jar \
#     /tmp/

# RUN ${KC_HOME_DIR}/bin/jboss-cli.sh --command="module add --name=io.github.pckhoi.keycloak.webhook --resources=/tmp/webhook-$KEYCLOAK_VERSION.$PROVIDERS_VERSION.jar --dependencies=org.hibernate,org.keycloak.keycloak-core,org.keycloak.keycloak-model-jpa,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,org.keycloak.keycloak-services"
