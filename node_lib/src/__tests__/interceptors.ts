import { AxiosResponse, AxiosError } from 'axios';

export const logResponse = (response: AxiosResponse) => {
  const { status, config } = response;
  console.info('response', {
    status,
    method: config.method?.toUpperCase(),
    url: [config.baseURL, config.url].join('/'),
  });
  return response;
};

export const logErrorResponse = (error: AxiosError) => {
  if (error.response) {
    const { status, config, headers, data } = error.response;
    console.error('error response', {
      status,
      method: config.method?.toUpperCase(),
      url: [config.baseURL, config.url].join('/'),
      body: data,
    });
  }
  return Promise.reject(error);
};
