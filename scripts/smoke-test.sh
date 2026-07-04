#!/usr/bin/env bash
set -euo pipefail

CICD_BASE_URL="${CICD_BASE_URL:-http://localhost:8082}"
GATEWAY_BASE_URL="${GATEWAY_BASE_URL:-http://localhost:9090}"

require_json_field() {
  local url="$1"
  local expected="$2"

  response="$(curl --fail --silent --show-error "$url")"
  if ! printf '%s' "$response" | grep -q "$expected"; then
    printf 'Unexpected response from %s\nExpected to find: %s\nActual: %s\n' "$url" "$expected" "$response" >&2
    exit 1
  fi
}

require_json_field "$CICD_BASE_URL/actuator/health/readiness" '"status":"UP"'
require_json_field "$CICD_BASE_URL/api/test" '"service":"cicd-service"'
require_json_field "$CICD_BASE_URL/api/deployments" '"environment":"ci"'

require_json_field "$GATEWAY_BASE_URL/actuator/health/readiness" '"status":"UP"'
require_json_field "$GATEWAY_BASE_URL/api/test" '"service":"gateway-service"'
require_json_field "$GATEWAY_BASE_URL/api/pipeline-summary" '"backendStatus":"ok"'

printf 'Smoke test passed for CICD service at %s and gateway service at %s\n' "$CICD_BASE_URL" "$GATEWAY_BASE_URL"
