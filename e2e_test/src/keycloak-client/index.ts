import axios, { AxiosResponse, AxiosInstance, AxiosError } from 'axios';

import { EventsConfig, User } from './keycloak';
import {
  Webhook,
  Filter,
  UserEventType,
  AdminEventOperationType,
  AdminEventResourceType,
} from './webhook-ext';

const handleError = (error: AxiosError): AxiosResponse<any, any> => {
  if (error.response) {
    // The request was made and the server responded with a status code
    // that falls out of the range of 2xx
    console.error(
      `${error.response.status} (${
        error.response.headers['content-type']
      }) ${JSON.stringify(error.response.data)}`,
    );
  }
  throw error;
};

export class Client {
  baseURL: string;
  realm: string;
  client: AxiosInstance;

  constructor(baseURL: string, realm: string, accessToken: string) {
    this.baseURL = baseURL;
    this.realm = realm;
    this.client = axios.create({
      headers: { Authorization: `Bearer ${accessToken}` },
    });
  }

  static async authenticate(
    baseURL: string,
    realm: string,
    clientID: string,
    clientSecret: string,
  ): Promise<Client> {
    type AuthenticateResponse = {
      access_token: string;
    };
    const resp: AxiosResponse<AuthenticateResponse, string> = await axios.post(
      `${baseURL}/realms/${realm}/protocol/openid-connect/token`,
      new URLSearchParams({
        grant_type: 'client_credentials',
        client_id: clientID,
        client_secret: clientSecret,
      }).toString(),
    );
    return new Client(baseURL, realm, resp.data.access_token);
  }

  public get realmEndpoint(): string {
    return `${this.baseURL}/realms/${this.realm}`;
  }

  public get webhooksEndpoint(): string {
    return `${this.baseURL}/realms/${this.realm}/pckhoi-webhook/webhooks`;
  }

  public get usersEndpoint(): string {
    return `${this.baseURL}/admin/realms/${this.realm}/users`;
  }

  public get eventsConfigEndpoint(): string {
    return `${this.baseURL}/admin/realms/${this.realm}/events/config`;
  }

  public async createWebhook(webhook: Webhook): Promise<string> {
    const resp: AxiosResponse<null, Webhook> = await this.client
      .post(this.webhooksEndpoint, webhook)
      .catch<AxiosResponse<null, Webhook>>(handleError);
    return resp.headers['location'];
  }

  public async listWebhooks(): Promise<Webhook[]> {
    const resp: AxiosResponse<Webhook[], null> = await this.client
      .get(this.webhooksEndpoint)
      .catch<AxiosResponse<Webhook[], null>>(handleError);
    return resp.data;
  }

  public async getWebhook(id: string): Promise<Webhook> {
    const resp: AxiosResponse<Webhook, null> = await this.client
      .get(`${this.webhooksEndpoint}/${id}`)
      .catch<AxiosResponse<Webhook, null>>(handleError);
    return resp.data;
  }

  public deleteWebhook(id: string): Promise<AxiosResponse<any, null>> {
    return this.client
      .delete(`${this.webhooksEndpoint}/${id}`)
      .catch<AxiosResponse<any, null>>(handleError);
  }

  public deleteAllWebhooks(): Promise<AxiosResponse<any, null>> {
    return this.client
      .delete(this.webhooksEndpoint)
      .catch<AxiosResponse<any, null>>(handleError);
  }

  public async createUser(user: User): Promise<string> {
    const resp: AxiosResponse<null, User> = await this.client
      .post(this.usersEndpoint, user)
      .catch<AxiosResponse<null, User>>(handleError);
    return resp.headers['location'];
  }

  public async deleteUser(id: string): Promise<AxiosResponse<any, null>> {
    return this.client
      .delete(`${this.usersEndpoint}/${id}`)
      .catch<AxiosResponse<any, null>>(handleError);
  }

  public async getEventsConfig(): Promise<EventsConfig> {
    const resp: AxiosResponse<EventsConfig, null> = await this.client
      .get(this.eventsConfigEndpoint)
      .catch<AxiosResponse<EventsConfig, null>>(handleError);
    return resp.data;
  }

  public async updateEventsConfig(
    updateCallback: (cfg: EventsConfig) => EventsConfig,
  ): Promise<void> {
    const cfg = await this.getEventsConfig();
    await this.client
      .put(this.eventsConfigEndpoint, updateCallback(cfg))
      .catch<AxiosResponse<null, EventsConfig>>(handleError);
  }
}

export const isRealmReady = (
  baseURL: string,
  realm: string,
): Promise<AxiosResponse<any, null>> =>
  axios
    .get(`${baseURL}/realms/${realm}`)
    .catch<AxiosResponse<any, null>>(handleError);
