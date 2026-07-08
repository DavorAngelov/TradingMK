import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '20s', target: 30 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<800'],
    http_req_failed: ['rate<0.05'],
  },
};

const BASE_URL = 'http://localhost:8080';

export function setup() {
  const res = http.post(`${BASE_URL}/api/auth/authenticate`, JSON.stringify({
    username: 'davor',
    password: 'davor',
  }), { headers: { 'Content-Type': 'application/json' } });

  check(res, { 'login succeeded': (r) => r.status === 200 });
  return { token: res.json('token') };
}

export default function (data) {
  const headers = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${data.token}`,
  };

  const stocksRes = http.get(`${BASE_URL}/api/stocks`);
  check(stocksRes, { 'stocks status 200': (r) => r.status === 200 });

  const tradeRes = http.post(`${BASE_URL}/api/trades/request`, JSON.stringify({
    stockSymbol: 'KMB',
    quantity: 1,
    pricePerUnit: 100.0,
    type: 'BUY',
  }), { headers });

  check(tradeRes, { 'trade request accepted': (r) => r.status === 200 });

  sleep(1);
}