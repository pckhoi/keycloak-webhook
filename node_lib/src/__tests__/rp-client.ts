import KeycloakAdminClient from '@keycloak/keycloak-admin-client';
import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { logErrorResponse, logResponse } from './interceptors';

type OpenIDConfig = {
  token_endpoint: string;
};

export type TokenResponse = {
  access_token?: string;
  id_token?: string;
};

export class RelyingPartyClient {
  clientID: string;
  clientSecret: string;
  axios: AxiosInstance;

  constructor(tokenEndpoint: string, clientID: string, clientSecret: string) {
    this.clientID = clientID;
    this.clientSecret = clientSecret;
    this.axios = axios.create({ baseURL: tokenEndpoint });
    this.axios.interceptors.response.use(logResponse, logErrorResponse);
  }

  public async authenticate(
    username: string,
    password: string,
  ): Promise<TokenResponse> {
    const params = new URLSearchParams();
    params.set('client_id', this.clientID);
    params.set('client_secret', this.clientSecret);
    params.set('grant_type', 'password');
    params.set('scope', 'openid');
    params.set('username', username);
    params.set('password', password);
    const resp: AxiosResponse<TokenResponse, string> = await this.axios.post(
      '',
      params.toString(),
    );
    return resp.data;
  }

  public static async discover(
    baseURL: string,
    realm: string,
    clientID: string,
    clientSecret: string,
  ): Promise<RelyingPartyClient> {
    const issuer = `${baseURL}/realms/${realm}`;
    const resp: AxiosResponse<OpenIDConfig, null> = await axios.get(
      `${issuer}/.well-known/openid-configuration`,
    );
    const tokenEndpoint = resp.data.token_endpoint;
    return new RelyingPartyClient(tokenEndpoint, clientID, clientSecret);
  }

  public static async createClient(
    kcClient: KeycloakAdminClient,
    keycloakURL: string,
    realm: string,
    clientID: string,
  ): Promise<RelyingPartyClient> {
    await kcClient.clients.create({
      clientId: clientID,
      realm,
      enabled: true,
      standardFlowEnabled: true,
      directAccessGrantsEnabled: true,
      frontchannelLogout: true,
      fullScopeAllowed: true,
      clientAuthenticatorType: 'client-secret',
      secret: 'change-me',
    });
    return RelyingPartyClient.discover(
      keycloakURL,
      realm,
      clientID,
      'change-me',
    );
  }
}
