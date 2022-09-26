import { Server } from 'http';

import express from 'express';
import audit from 'express-requests-logger';

class Logger {
  name: string;
  constructor(name: string) {
    this.name = name;
  }
  info(obj: any) {
    console.log(`${this.name}: ${JSON.stringify(obj)}`);
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
