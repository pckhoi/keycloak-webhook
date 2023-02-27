import querystring from 'querystring';
import { AddressInfo } from 'net';
import path from 'path';
import { promisify } from 'util';
import KeycloakAdminClient from '@keycloak/keycloak-admin-client';
import {
  describe,
  beforeAll,
  beforeEach,
  it,
  expect,
  afterAll,
} from '@jest/globals';

import { RelyingPartyClient } from './rp-client';
import {
  AdminEventOperationType,
  AdminEventResourceType,
  UserEventType,
} from '../enums';
import { startServer, waitForKeycloakToBecomeReady } from './server';
import axios, { AxiosResponse } from 'axios';
import { Client } from '../client';
import { logErrorResponse, logResponse } from './interceptors';

const createKeycloakClient = async (keycloakURL: string) => {
  const kcClient = new KeycloakAdminClient({
    baseUrl: keycloakURL,
    realmName: 'master',
    requestArgOptions: { catchNotFound: false },
  });
  await kcClient.auth({
    grantType: 'password',
    username: 'admin',
    password: 'admin',
    clientId: 'admin-cli',
  });
  return kcClient;
};

const createWebhookClient = async (
  keycloakURL: string,
  kcClient: KeycloakAdminClient,
  realm: string,
) => {
  await kcClient.clients.create({
    realm,
    id: 'keycloak-webhook',
    name: 'keycloak-webhook',
    enabled: true,
    serviceAccountsEnabled: true,
    authorizationServicesEnabled: true,
    publicClient: false,
    clientAuthenticatorType: 'client-secret',
    secret: 'keycloak-webhook',
  });
  const discoveryResp: AxiosResponse<{ token_endpoint: string }> =
    await axios.get(
      `${keycloakURL}/realms/${realm}/.well-known/uma2-configuration`,
    );
  const tokenResp: AxiosResponse<{ access_token: string }> = await axios.post(
    discoveryResp.data.token_endpoint,
    querystring.stringify({
      grant_type: 'client_credentials',
      client_id: 'keycloak-webhook',
      client_secret: 'keycloak-webhook',
    }),
  );
  return new Client(keycloakURL, realm, tokenResp.data.access_token, [
    [logResponse, logErrorResponse],
  ]);
};

describe('webhook rest api', () => {
  const baseURL = 'http://localhost:8080';
  let kcClient: KeycloakAdminClient;
  let whClient: Client;
  const realm = 'test-realm';

  beforeAll(async () => {
    await waitForKeycloakToBecomeReady(baseURL);
    kcClient = await createKeycloakClient(baseURL);
    kcClient.setConfig({ realmName: realm });
    await kcClient.realms.create({
      realm,
      userManagedAccessAllowed: true,
      enabled: true,
    });
    whClient = await createWebhookClient(baseURL, kcClient, realm);
  });

  describe('rest api', () => {
    let webhookID: string;
    describe('post /webhooks', () => {
      it('should create webhook', async () => {
        const webhookURI = await whClient.create({
          name: 'My webhook',
          url: 'http://localhost:1234/webhook',
          filters: [
            {
              userEventType: UserEventType.REGISTER,
            },
          ],
        });
        await whClient.create({
          name: 'Another webhook',
          url: 'http://localhost:1234/webhook1',
          filters: [
            {
              userEventType: UserEventType.DELETE_ACCOUNT,
            },
          ],
        });
        expect(webhookURI).toBeTruthy();
        webhookID = path.basename(webhookURI);
        expect(webhookID).toBeTruthy();
      });
    });

    describe('get /webhooks/{id}', () => {
      it('should fetch single webhook', async () => {
        const webhook = await whClient.get(webhookID);
        expect(webhook).toBeTruthy();
        expect(webhook.id).toEqual(webhookID);
        expect(webhook.filters).toHaveLength(1);
        expect(webhook.filters[0].userEventType).toEqual(
          UserEventType.REGISTER,
        );
      });
    });

    describe('get /webhooks', () => {
      it('should fetch all webhook', async () => {
        const webhooks = await whClient.list();
        expect(webhooks).toHaveLength(2);
      });
    });

    describe('delete /webhooks/{id}', () => {
      it('should delete webhook', async () => {
        await whClient.del(webhookID);
        const webhook = await whClient.get(webhookID);
        expect(webhook).toBeFalsy();
      });
    });

    describe('delete /webhooks', () => {
      it('should delete all webhooks', async () => {
        await whClient.delAll();
        const webhooks = await whClient.list();
        expect(webhooks).toHaveLength(0);
      });
    });
  });

  describe('event handler', () => {
    type adminEventResponse = {
      authDetails: {
        realmId: string;
        clientId: string;
        userId: string;
        ipAddress: string;
      };
      id: string;
      operationType: string;
      resourceType: string;
      representation?: Record<string, any>;
      resourcePath: string;
      time: number;
    };

    type userEventResponse = {
      details: Record<string, any>;
      time: number;
      type: string;
      userId: string;
      clientId: string;
    };

    const received: Map<number, Record<string, any>[]> = new Map();
    let serverURL: string;
    let stopServer: () => Promise<void>;
    let rpClient: RelyingPartyClient;

    beforeAll(async () => {
      rpClient = await RelyingPartyClient.createClient(
        kcClient,
        baseURL,
        realm,
        'public-client',
      );

      const eventsConfig = await kcClient.realms.getConfigEvents({ realm });
      eventsConfig.eventsEnabled = true;
      eventsConfig.eventsListeners = [
        ...(eventsConfig.eventsListeners || []),
        'pckhoi-webhook',
      ];
      // eventsConfig.enabledEventTypes = eventsConfig.enabledEventTypes;
      eventsConfig.adminEventsEnabled = true;
      eventsConfig.adminEventsDetailsEnabled = true;
      await kcClient.realms.updateConfigEvents({ realm }, eventsConfig);

      const server = await startServer(
        (num: number, data: Record<string, any>) => {
          received.set(num, [...(received.get(num) || []), data]);
        },
      );
      serverURL = `http://host.docker.internal:${
        (server.address() as AddressInfo).port
      }`;
      stopServer = promisify(server.close.bind(server));
    });

    afterAll(async () => {
      await stopServer();
    });

    beforeEach(() => {
      received.clear();
    });

    it('should send admin event', async () => {
      await whClient.create({
        name: 'User creation',
        url: `${serverURL}/webhook/1`,
        filters: [
          {
            adminEventResourceType: AdminEventResourceType.USER,
            adminEventOperationType: AdminEventOperationType.CREATE,
          },
        ],
      });
      await whClient.create({
        name: 'User removal',
        url: `${serverURL}/webhook/2`,
        filters: [
          {
            adminEventResourceType: AdminEventResourceType.USER,
            adminEventOperationType: AdminEventOperationType.DELETE,
          },
        ],
      });
      await whClient.create({
        name: 'User login',
        url: `${serverURL}/webhook/3`,
        filters: [{ userEventType: UserEventType.LOGIN }],
      });
      await whClient.create({
        name: 'User admin events',
        url: `${serverURL}/webhook/4`,
        filters: [{ adminEventResourceType: AdminEventResourceType.USER }],
      });
      await whClient.create({
        name: 'User creation and removal',
        url: `${serverURL}/webhook/5`,
        filters: [
          {
            adminEventResourceType: AdminEventResourceType.USER,
            adminEventOperationType: AdminEventOperationType.CREATE,
          },
          {
            adminEventResourceType: AdminEventResourceType.USER,
            adminEventOperationType: AdminEventOperationType.DELETE,
          },
        ],
      });

      const user = await kcClient.users.create({
        realm,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
        username: 'johndoe',
        enabled: true,
        credentials: [
          {
            type: 'password',
            userLabel: 'My password',
            temporary: false,
            value: 'password',
          },
        ],
      });

      const tokSet = await rpClient.authenticate('johndoe', 'password');
      expect(tokSet.access_token).toBeTruthy();
      expect(tokSet.id_token).toBeTruthy();

      await kcClient.users.del({ realm, id: user.id });

      let adminEvents: adminEventResponse[] = received.get(
        1,
      ) as adminEventResponse[];
      expect(adminEvents).toHaveLength(1);
      expect(adminEvents[0].operationType).toEqual('CREATE');
      expect(adminEvents[0].resourcePath).toEqual(`users/${user.id}`);
      expect(adminEvents[0].representation?.username).toEqual('johndoe');

      adminEvents = received.get(2) as adminEventResponse[];
      expect(adminEvents).toHaveLength(1);
      expect(adminEvents[0].operationType).toEqual('DELETE');
      expect(adminEvents[0].resourcePath).toEqual(`users/${user.id}`);

      const userEvents: userEventResponse[] = received.get(
        3,
      ) as userEventResponse[];
      expect(userEvents).toHaveLength(1);
      expect(userEvents[0].type).toEqual('LOGIN');

      adminEvents = received.get(4) as adminEventResponse[];
      expect(adminEvents).toHaveLength(2);
      expect(adminEvents[0].operationType).toEqual('CREATE');
      expect(adminEvents[1].operationType).toEqual('DELETE');

      adminEvents = received.get(5) as adminEventResponse[];
      expect(adminEvents).toHaveLength(2);
      expect(adminEvents[0].operationType).toEqual('CREATE');
      expect(adminEvents[1].operationType).toEqual('DELETE');
    });
  });
});
