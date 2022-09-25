import { asyncify, retry as plainRetry, RetryOptions } from 'async';

export const retry = (options: RetryOptions<Error>, task: () => Promise<any>) =>
  new Promise((resolve, reject) => {
    plainRetry(options, asyncify(task), (err, result) => {
      if (err) {
        reject(err);
      } else {
        resolve(result);
      }
    });
  });
