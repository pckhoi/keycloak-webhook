import axios, { AxiosInstance, AxiosResponse, AxiosError } from 'axios';
import {
  UserEventType,
  AdminEventResourceType,
  AdminEventOperationType,
} from './enums';

export interface Filter {
  id?: string;
  userEventType?: UserEventType;
  adminEventResourceType?: AdminEventResourceType;
  adminEventOperationType?: AdminEventOperationType;
}

export interface Webhook {
  id?: string;
  name: string;
  url: string;
  filters: Filter[];
}

export type ResponseInterceptor = [
  (
    | ((
        value: AxiosResponse<any, any>,
      ) => AxiosResponse<any, any> | Promise<AxiosResponse<any, any>>)
    | undefined
  ),
  (error: AxiosError) => any,
];

export class Client {
  private axios: AxiosInstance;

  constructor(
    kcURL: string,
    realm: string,
    accessToken: string,
    interceptors?: ResponseInterceptor[],
  ) {
    this.axios = axios.create({
      baseURL: `${kcURL}/realms/${realm}/pckhoi-webhook/webhooks`,
      headers: { Authorization: `Bearer ${accessToken}` },
    });
    if (interceptors) {
      for (const [onFulfilled, onRejected] of interceptors) {
        this.axios.interceptors.response.use(onFulfilled, onRejected);
      }
    }
  }

  public async create(webhook: Webhook): Promise<string> {
    const resp: AxiosResponse<null, Webhook> = await this.axios.post(
      '',
      webhook,
    );
    return resp.headers['location'];
  }

  public async list(): Promise<Webhook[]> {
    const resp: AxiosResponse<Webhook[], null> = await this.axios.get('');
    return resp.data;
  }

  public async get(id: string): Promise<Webhook> {
    const resp: AxiosResponse<Webhook, null> = await this.axios.get(
      `${''}/${id}`,
    );
    return resp.data;
  }

  public del(id: string): Promise<AxiosResponse<any, null>> {
    return this.axios.delete(`${''}/${id}`);
  }

  public delAll(): Promise<AxiosResponse<any, null>> {
    return this.axios.delete('');
  }
}
