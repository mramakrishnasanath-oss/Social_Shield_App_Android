// SocialShield k6 Load Test Suite
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 100,
  duration: '1m',
  thresholds: {
    http_req_failed: ['rate<0.05'], // Request failure rate must be under 5%
    http_req_duration: ['p(95)<1500'], // 95% of requests must complete in under 1.5 seconds (1500ms)
  },
};

export default function () {
  const baseUrl = __ENV.BACKEND_URL || 'http://localhost:8000';
  
  // Primary target endpoints check
  const healthRes = http.get(`${baseUrl}/health`);
  const rootRes = http.get(`${baseUrl}/`);

  check(healthRes, {
    'health endpoint status is 200': (r) => r.status === 200,
  });

  check(rootRes, {
    'root endpoint status is 200': (r) => r.status === 200,
  });

  sleep(0.1);
}
