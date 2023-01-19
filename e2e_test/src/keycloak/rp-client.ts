import axios, { AxiosResponse } from 'axios';

type OpenIDConfig = {
  token_endpoint: string;
};

export type TokenResponse = {
  access_token?: string;
  id_token?: string;
};

export class RelyingPartyClient {
  tokenEndpoint: string;
  clientID: string;
  clientSecret: string;

  constructor(tokenEndpoint: string, clientID: string, clientSecret: string) {
    this.tokenEndpoint = tokenEndpoint;
    this.clientID = clientID;
    this.clientSecret = clientSecret;
  }

  public static async discover(
    baseURL: string,
    realm: string,
    clientID: string,
    clientSecret: string,
  ): Promise<RelyingPartyClient> {
    const issuer = `${baseURL}/auth/realms/${realm}`;
    const resp: AxiosResponse<OpenIDConfig, null> = await axios.get(
      `${issuer}/.well-known/openid-configuration`,
    );
    const tokenEndpoint = resp.data.token_endpoint;
    return new RelyingPartyClient(tokenEndpoint, clientID, clientSecret);
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
    const resp: AxiosResponse<TokenResponse, string> = await axios.post(
      this.tokenEndpoint,
      params.toString(),
      { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } },
    );
    return resp.data;
  }
}
