import { Server } from 'http';

import express from 'express';
import audit from 'express-requests-logger';
import axios, { AxiosResponse } from 'axios';

class Logger {
  name: string;
  constructor(name: string) {
    this.name = name;
  }
  info(obj: any) {
    // console.log(`${this.name}: ${JSON.stringify(obj)}`);
  }
}

export const startServer = (
  onWebhookCalled: (num: number, data: Record<string, any>) => void,
): Promise<Server<any, any>> =>
  new Promise((resolve, reject) => {
    const app = express();
    app.use(express.json());
    app.use(
      audit({
        doubleAudit: true,
        logger: new Logger('test-server'),
      }),
    );
    app.post('/webhook/:num', (req, res) => {
      onWebhookCalled(parseInt(req.params.num), req.body);
      res.status(200).json({ ack: true });
    });
    const server = app.listen(0, () => {
      resolve(server);
    });
  });

export const envVar = (name: string, defaultValue?: string): string => {
  if (!process.env[name]) {
    if (defaultValue) return defaultValue;
    throw new Error(`environment variable not defined: ${name}`);
  }
  return process.env[name] as string;
};

export const waitForKeycloakToBecomeReady = async (
  keycloakURL: string,
): Promise<void> => {
  const intervalSeconds = parseInt(
    envVar('KEYCLOAK_WAIT_INTERVAL_SECONDS', '5'),
  );
  const maxRetries = parseInt(envVar('KEYCLOAK_WAIT_MAX_RETRIES', '12'));
  const realmURL = `${keycloakURL}/realms/master`;
  for (let i = 0; i < maxRetries; i++) {
    try {
      await axios.get(realmURL);
      return;
    } catch (err) {
      // pass
    }
    await new Promise((r) => setTimeout(r, intervalSeconds * 1000));
  }
  throw new Error('timeout waiting for Keycloak to become ready');
};
