import { spawn } from 'child_process';
import { basename } from 'path';

const runCommand = (cmd: string, args: string[], printOutput?: boolean) =>
  new Promise((resolve, reject) => {
    const child = spawn(cmd, args, {
      cwd: basename(__dirname),
    });
    if (printOutput) {
      child.stdout.on('data', (data: Buffer) => {
        console.log(data.toString());
      });

      child.stderr.on('data', (data: Buffer) => {
        console.error(data.toString());
      });
    }
    child.on('error', (error) => {
      reject(error);
    });
    child.on('exit', (code, signal) => {
      if (code !== 0) {
        reject(`non-zero exit code: ${code as number}`);
        return;
      }
      resolve(null);
    });
  });

export const up = () => runCommand('docker-compose', ['up', '-d']);

export const down = () => runCommand('docker-compose', ['down']);

export const logs = () =>
  runCommand('docker-compose', ['logs', 'keycloak'], true);
