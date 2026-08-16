#!/usr/bin/env bash
# Smoke del contrato JWT del starter kit. Requiere la app escuchando.
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
WORKDIR="${TMPDIR:-/tmp}/joko-smoke-$$"
mkdir -p "$WORKDIR"
trap 'rm -rf "$WORKDIR"' EXIT

json_field() {
  python3 -c 'import json,sys; print(json.load(sys.stdin).get(sys.argv[1],"") or "")' "$1"
}

http_get() {
  local path="$1" out="$2"
  shift 2
  curl -sS -o "$out" -w '%{http_code}' "$@" "${BASE_URL}${path}"
}

fail() { echo "FAIL: $*" >&2; exit 1; }

echo "Smoke contra ${BASE_URL}"

code="$(http_get /api/countries "$WORKDIR/countries.json")"
[[ "$code" == "200" ]] || fail "GET /api/countries -> $code"
grep -q Paraguay "$WORKDIR/countries.json" || fail "countries sin Paraguay"
echo "  GET /api/countries            $code"

code="$(curl -sS -o "$WORKDIR/login.json" -w '%{http_code}' \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}' \
  "${BASE_URL}/api/login")"
[[ "$code" == "200" ]] || fail "POST /api/login -> $code $(cat "$WORKDIR/login.json")"
REFRESH="$(json_field secret < "$WORKDIR/login.json")"
[[ -n "$REFRESH" ]] || fail "login sin secret"
echo "  POST /api/login               $code"

code="$(curl -sS -o "$WORKDIR/access.json" -w '%{http_code}' \
  -X POST -H "X-JOKO-AUTH: ${REFRESH}" \
  "${BASE_URL}/api/token/user-access")"
[[ "$code" == "200" ]] || fail "POST /api/token/user-access -> $code $(cat "$WORKDIR/access.json")"
ACCESS="$(json_field secret < "$WORKDIR/access.json")"
[[ -n "$ACCESS" ]] || fail "access sin secret"
echo "  POST /api/token/user-access   $code"

code="$(curl -sS -o "$WORKDIR/user.json" -w '%{http_code}' \
  -H "X-JOKO-AUTH: ${ACCESS}" \
  "${BASE_URL}/api/secure/users/admin")"
[[ "$code" == "200" ]] || fail "GET /api/secure/users/admin -> $code $(cat "$WORKDIR/user.json")"
grep -q admin "$WORKDIR/user.json" || fail "user sin admin"
echo "  GET /api/secure/users/admin   $code"

code="$(curl -sS -o /dev/null -w '%{http_code}' "${BASE_URL}/api/secure/users/admin")"
[[ "$code" == "401" ]] || fail "sin token esperaba 401, fue $code"
echo "  GET /api/secure/users/admin   $code (sin token)"

code="$(curl -sS -o /dev/null -w '%{http_code}' "${BASE_URL}/v3/api-docs")"
[[ "$code" == "200" ]] || fail "GET /v3/api-docs -> $code"
echo "  GET /v3/api-docs              $code"

echo "OK"
