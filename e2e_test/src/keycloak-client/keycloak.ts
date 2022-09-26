export enum DecisionStrategy {
  Affirmative = 'AFFIRMATIVE',
  Unanimous = 'UNANIMOUS',
  Consensus = 'CONSENSUS',
}

export enum Logic {
  Positive = 'POSITIVE',
  Negative = 'NEGATIVE',
}

export enum EnforcementMode {
  Enforcing = 'ENFORCING',
  Permissive = 'PERMISSIVE',
  Disabled = 'DISABLED',
}

export type User = {
  access?: Record<string, boolean>;
  attributes?: Record<string, string>;
  clientConsents?: UserConsent[];
  clientRoles?: Record<string, string>[];
  createdTimestamp?: number;
  credentials?: Credential[];
  disableableCredentialTypes?: string[];
  email?: string;
  emailVerified?: boolean;
  enabled?: boolean;
  federatedIdentities?: FederatedIdentity[];
  federationLink?: string;
  firstName?: string;
  groups?: string[];
  id?: string;
  lastName?: string;
  notBefore?: number;
  origin?: string;
  realmRoles?: string[];
  requiredActions?: string[];
  self?: string;
  serviceAccountClientId?: string;
  username?: string;
};

export type Policy = {
  config?: Record<string, string>;
  decisionStrategy?: DecisionStrategy;
  description?: string;
  id?: string;
  logic?: Logic;
  name?: string;
  owner?: string;
  policies?: string[];
  resources?: string[];
  resourcesData?: Resource[];
  scopes?: string[];
  scopesData?: Scope[];
  type?: string;
};

export type Scope = {
  displayName?: string;
  iconUri?: string;
  id?: string;
  name?: string;
  policies?: Policy[];
  resources?: Resource[];
};

export type Resource = {
  id?: string;
  attributes?: Record<string, string>;
  displayName?: string;
  icon_uri?: string;
  name?: string;
  ownerManagedAccess?: boolean;
  scopes?: Scope[];
  type?: string;
  uris?: string[];
};

export type ResourceServer = {
  allowRemoteResourceManagement?: boolean;
  clientId?: string;
  decisionStrategy?: DecisionStrategy;
  id?: string;
  name?: string;
  policies?: Policy[];
  policyEnforcementMode?: EnforcementMode;
  resources?: Resource[];
  scopes?: Scope[];
};

export type ProtocolMapper = {
  config?: Record<string, string>;
  id?: string;
  name?: string;
  protocol?: string;
  protocolMapper?: string;
};

export type Client = {
  access?: Record<string, boolean>;
  adminUrl?: string;
  alwaysDisplayInConsole?: boolean;
  attributes?: Record<string, string>;
  authenticationFlowBindingOverrides?: Record<string, any>;
  authorizationServicesEnabled?: boolean;
  authorizationSettings?: ResourceServer;
  baseUrl?: string;
  bearerOnly?: boolean;
  clientAuthenticatorType?: string;
  clientId?: string;
  consentRequired?: boolean;
  defaultClientScopes?: string[];
  description?: string;
  directAccessGrantsEnabled?: boolean;
  enabled?: boolean;
  frontchannelLogout?: boolean;
  fullScopeAllowed?: boolean;
  id?: string;
  implicitFlowEnabled?: boolean;
  name?: string;
  nodeReRegistrationTimeout?: number;
  notBefore?: number;
  oauth2DeviceAuthorizationGrantEnabled?: boolean;
  optionalClientScopes?: string[];
  origin?: string;
  protocol?: string;
  protocolMappers?: ProtocolMapper[];
  publicClient?: boolean;
  redirectUris?: string[];
  registeredNodes?: Record<string, any>;
  registrationAccessToken?: string;
  rootUrl?: string;
  secret?: string;
  serviceAccountsEnabled?: boolean;
  standardFlowEnabled?: boolean;
  surrogateAuthRequired?: boolean;
  webOrigins?: string[];
};

export type UserConsent = {
  clientId?: string;
  createdDate?: number;
  grantedClientScopes?: string[];
  lastUpdatedDate?: number;
};

export type Credential = {
  createdDate?: number;
  credentialData?: string;
  id?: string;
  priority?: number;
  secretData?: string;
  temporary?: boolean;
  type?: string;
  userLabel?: string;
  value?: string;
};

export type FederatedIdentity = {
  identityProvider?: string;
  userId?: string;
  userName?: string;
};

export type Group = {
  access?: Record<string, boolean>;
  attributes?: Record<string, string>;
  clientRoles?: Record<string, string[]>;
  id?: string;
  name?: string;
  path?: string;
  realmRoles?: string[];
  subGroups?: Group[];
};

export type RoleComposites = {
  client?: Record<string, string>;
  realm?: string[];
};

export type Role = {
  attributes?: Record<string, string>;
  clientRole?: boolean;
  composite?: boolean;
  composites?: RoleComposites;
  containerId?: string;
  description?: string;
  id?: string;
  name?: string;
};

export type Mapping = {
  clientMappings?: Record<string, any>;
  realmMappings?: Role[];
};

export type Impersonation = {
  redirect?: string;
  sameRealm?: boolean;
};

export type ManagementPermission = {
  enabled?: boolean;
  resource?: string;
  scopePermissions?: Record<string, string>;
};

export type EventsConfig = {
  adminEventsDetailsEnabled?: boolean;
  adminEventsEnabled?: boolean;
  enabledEventTypes?: string[];
  eventsEnabled?: boolean;
  eventsExpiration?: number;
  eventsListeners?: string[];
};
