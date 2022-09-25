import path from 'path';
import { up, down, logs } from '../docker-compose';

import { Client, isRealmReady } from '../keycloak-client';
import { retry } from '../retry';

describe('webhook rest api', () => {
  const baseURL = 'http://localhost:8080';
  const realm = 'test-realm';
  const clientID = 'admin-client';
  const clientSecret = 'change-me';
  let client: Client;

  beforeAll(async () => {
    await up();
    await retry({ times: 50, interval: 1000 }, () =>
      isRealmReady(baseURL, realm),
    );
    client = await Client.authenticate(baseURL, realm, clientID, clientSecret);
    console.log('authenticated!');
  });

  afterAll(async () => {
    await logs();
    await down();
  });

  let webhookID: string;
  describe('post /webhooks', () => {
    it('should create webhook', async () => {
      const webhookURI = await client.createWebhook({
        name: 'My webhook',
        url: 'http://localhost:1234/webhook',
        filters: [
          {
            userEventType: 'REGISTER',
          },
        ],
      });
      expect(webhookURI).toBeTruthy();
      // const bits = webhookURI.split('/')
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
    });
  });

  describe('get /webhooks', () => {
    it('should fetch all webhook', async () => {
      await client.createWebhook({
        name: 'Another webhook',
        url: 'http://localhost:1234/webhook1',
        filters: [
          {
            userEventType: 'REGISTER',
          },
        ],
      });
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
