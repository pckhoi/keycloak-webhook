import express from 'express';
import { Server } from 'http';

export const startServer = (
  onWebhookCalled: (num: number, data: Record<string, any>) => void,
): Promise<Server<any, any>> =>
  new Promise((resolve, reject) => {
    const app = express();
    app.use(express.json());
    app.post('/webhook/:num', (req, res) => {
      onWebhookCalled(parseInt(req.params.num), req.body);
      res.sendStatus(200);
    });
    const server = app.listen(0, () => {
      resolve(server);
    });
  });
