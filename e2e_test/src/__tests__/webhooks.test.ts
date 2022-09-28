import { assert } from 'console';
import { AddressInfo } from 'net';
import path from 'path';
import { promisify } from 'util';
import { up, down, logs } from '../docker-compose';

import { AdminClient, isRealmReady } from '../keycloak/admin-client';
import { EventsConfig } from '../keycloak/keycloak';
import { RelyingPartyClient } from '../keycloak/rp-client';
import {
  AdminEventOperationType,
  AdminEventResourceType,
  UserEventType,
} from '../keycloak/webhook-ext';
import { retry } from '../retry';
import { startServer } from '../server';

describe('webhook rest api', () => {
  const baseURL = 'http://localhost:8080';
  const realm = 'test-realm';
  const clientID = 'admin-client';
  const clientSecret = 'change-me';
  let client: AdminClient;

  beforeAll(async () => {
    await up();
    await retry({ times: 50, interval: 1000 }, () =>
      isRealmReady(baseURL, realm),
    );
    client = await AdminClient.authenticate(
      baseURL,
      realm,
      clientID,
      clientSecret,
    );
  });

  afterAll(async () => {
    await logs();
    await down();
  });

  describe('rest api', () => {
    let webhookID: string;
    describe('post /webhooks', () => {
      it('should create webhook', async () => {
        const webhookURI = await client.createWebhook({
          name: 'My webhook',
          url: 'http://localhost:1234/webhook',
          filters: [
            {
              userEventType: UserEventType.Register,
            },
          ],
        });
        await client.createWebhook({
          name: 'Another webhook',
          url: 'http://localhost:1234/webhook1',
          filters: [
            {
              userEventType: UserEventType.DeleteAccount,
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
        const webhook = await client.getWebhook(webhookID);
        expect(webhook).toBeTruthy();
        expect(webhook.id).toEqual(webhookID);
        expect(webhook.filters).toHaveLength(1);
        expect(webhook.filters[0].userEventType).toEqual(
          UserEventType.Register,
        );
      });
    });

    describe('get /webhooks', () => {
      it('should fetch all webhook', async () => {
        const webhooks = await client.listWebhooks();
        expect(webhooks).toHaveLength(2);
      });
    });

    describe('delete /webhooks/{id}', () => {
      it('should delete webhook', async () => {
        await client.deleteWebhook(webhookID);
        const webhook = await client.getWebhook(webhookID);
        expect(webhook).toBeFalsy();
      });
    });

    describe('delete /webhooks', () => {
      it('should delete all webhooks', async () => {
        await client.deleteAllWebhooks();
        const webhooks = await client.listWebhooks();
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

    const received: Map<number, Record<string, any>> = new Map();
    let serverURL: string;
    let stopServer: () => Promise<void>;
    let rpClient: RelyingPartyClient;

    beforeAll(async () => {
      rpClient = await client.createRelyingPartyClient('public-client');
      await client.updateEventsConfig(
        (cfg) =>
          ({
            ...cfg,
            eventsEnabled: true,
            eventsListeners: [...(cfg.eventsListeners || []), 'pckhoi-webhook'],
            enabledEventTypes: cfg.enabledEventTypes,
            adminEventsEnabled: true,
            adminEventsDetailsEnabled: true,
          } as EventsConfig),
      );
      const server = await startServer((num, data) => {
        received.set(num, data);
      });
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
      await client.createWebhook({
        name: 'User creation',
        url: `${serverURL}/webhook/1`,
        filters: [
          {
            adminEventResourceType: AdminEventResourceType.User,
            adminEventOperationType: AdminEventOperationType.Create,
          },
        ],
      });
      await client.createWebhook({
        name: 'User removal',
        url: `${serverURL}/webhook/2`,
        filters: [
          {
            adminEventResourceType: AdminEventResourceType.User,
            adminEventOperationType: AdminEventOperationType.Delete,
          },
        ],
      });
      await client.createWebhook({
        name: 'User login',
        url: `${serverURL}/webhook/3`,
        filters: [{ userEventType: UserEventType.Login }],
      });
      const userURI = await client.createUser({
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
      const userID = path.basename(userURI);

      const tokSet = await rpClient.authenticate('johndoe', 'password');
      expect(tokSet.access_token).toBeTruthy();
      expect(tokSet.id_token).toBeTruthy();

      await client.deleteUser(userID);

      let adminEventData: adminEventResponse = received.get(
        1,
      ) as adminEventResponse;
      expect(adminEventData).toBeTruthy();
      expect(adminEventData.operationType).toEqual('CREATE');
      expect(adminEventData.resourcePath).toEqual(`users/${userID}`);
      expect(adminEventData.representation?.username).toEqual('johndoe');

      adminEventData = received.get(2) as adminEventResponse;
      expect(adminEventData).toBeTruthy();
      expect(adminEventData.operationType).toEqual('DELETE');
      expect(adminEventData.resourcePath).toEqual(`users/${userID}`);

      const userEventData: userEventResponse = received.get(
        3,
      ) as userEventResponse;
      expect(userEventData.type).toEqual('LOGIN');
    });
  });
});
