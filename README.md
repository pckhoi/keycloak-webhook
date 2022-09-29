# Keycloak Webhook

Webhook extension for Keycloak

## REST API

Check out [openapi.yml](openapi.yml)

## Run end-to-end tests

```bash
cd e2e_test
npm run test
```

## Release

Deploy to OSSRH

```bash
mvn clean deploy -P release
```

[Release from OSSRH](https://central.sonatype.org/publish/release/)
