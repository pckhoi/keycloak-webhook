/** @type {import('ts-jest').JestConfigWithTsJest} */
export default {
  preset: 'ts-jest/presets/default-esm',
  testEnvironment: 'node',
  moduleNameMapper: {
    '^(\\.{1,2}/.*)\\.js$': '$1',
  },
  extensionsToTreatAsEsm: ['.ts'],
  testRegex: '/__tests__/.*(\\.|/)(test|spec)\\.[jt]sx?$',
  transform: {
    '^.+\\.(t|j)s$': [
      'ts-jest',
      {
        useESM: true,
        tsconfig: {
          allowJs: true,
        },
      },
    ],
  },
  transformIgnorePatterns: ['/node_modules/(?!@keycloak/)'],
};
