import http from 'k6/http';
import { sleep } from 'k6';

const url = 'http://localhost:8080/api/source/2c558875-8787-433e-bc6a-86dd4f3d6811/event';

export default function () {
  const payload = { name: 'foo' };

  let res = http.post(url, JSON.stringify(payload), {
    headers: { 'Content-Type': 'application/json' },
  });
  sleep(1);
}

