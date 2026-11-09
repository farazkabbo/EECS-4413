#!/usr/bin/env bash
# =====================================================================
# Fritz EV - curl test script for the back-end REST API.
# Owner: Ronak (PB-33). Mohammed's endpoints (catalogue, hot deals,
# validation, admin guard) are filled in; add your own sections below.
#
# Usage:   ./tests/curl/api-tests.sh                    (local, http://localhost:8080)
#          BASE_URL=https://fritz-ev-api.onrender.com ./tests/curl/api-tests.sh
# Needs:   bash + curl. Start the back end first (cd backend && ./mvnw spring-boot:run).
# =====================================================================
BASE_URL="${BASE_URL:-http://localhost:8080}"
COOKIES="$(mktemp)"
PASS=0; FAIL=0

# check <description> <expected-status> <curl args...>
check() {
  local desc="$1" expected="$2"; shift 2
  local status
  status=$(curl -s -o /tmp/fritz_body.json -w '%{http_code}' -b "$COOKIES" -c "$COOKIES" "$@")
  if [ "$status" = "$expected" ]; then
    PASS=$((PASS+1)); echo "PASS [$status] $desc"
  else
    FAIL=$((FAIL+1)); echo "FAIL [$status, expected $expected] $desc"; head -c 300 /tmp/fritz_body.json; echo
  fi
}

echo "== Health =="
check "health endpoint is UP"                               200 "$BASE_URL/actuator/health"

echo "== Catalogue (T09) =="
check "list all vehicles"                                   200 "$BASE_URL/api/vehicles"
check "filter by brand + sort by price"                     200 "$BASE_URL/api/vehicles?brand=Tesla&sort=price_asc"
check "filter used, accident free, SUV"                     200 "$BASE_URL/api/vehicles?condition=USED&accidentFree=true&shape=SUV"
check "vehicle details"                                     200 "$BASE_URL/api/vehicles/2"
check "unknown vehicle -> 404"                              404 "$BASE_URL/api/vehicles/99999"
check "non-numeric id -> 400"                               400 "$BASE_URL/api/vehicles/abc"

echo "== Hot deals =="
check "hot deals"                                           200 "$BASE_URL/api/vehicles/hot-deals?limit=3"
check "hot deals limit=0 -> 400"                            400 "$BASE_URL/api/vehicles/hot-deals?limit=0"

echo "== SQL injection (T10) =="
check "quote injection in brand -> 400"                     400 -G "$BASE_URL/api/vehicles" --data-urlencode "brand=Tesla' OR '1'='1"
check "DROP TABLE in sort -> 400"                           400 -G "$BASE_URL/api/vehicles" --data-urlencode "sort=price; DROP TABLE vehicles"
check "catalogue still intact afterwards"                   200 "$BASE_URL/api/vehicles"

echo "== Input validation =="
check "malformed JSON -> 400"                               400 -X POST "$BASE_URL/api/calculators/loan" -H 'Content-Type: application/json' -d '{oops'
check "invalid loan fields -> 400"                          400 -X POST "$BASE_URL/api/calculators/loan" -H 'Content-Type: application/json' -d '{"price":-1,"months":0}'

echo "== Admin guard (T12) =="
check "sales report without login -> 401"                   401 "$BASE_URL/api/admin/reports/sales?year=2026&month=10"
check "dashboard without login -> 401"                      401 "$BASE_URL/api/admin/dashboard"
# TODO(Ronak/Zaaid): once login works, sign in as bob (expect 403) and as admin (expect 200):
# check "login as admin" 200 -X POST "$BASE_URL/api/auth/login" -H 'Content-Type: application/json' -d '{"username":"admin","password":"Admin123!"}'

# ---------------------------------------------------------------------
# TODO(Zaaid):  register / login / logout, cart, checkout x3 (approve, approve, deny), reviews
# TODO(Tamjeed): compare, options, configuration price, loan, savings estimate
# TODO(Ronak):   test-drive slots / booking / double booking (409), reports as admin
# ---------------------------------------------------------------------

rm -f "$COOKIES"
echo
echo "Passed: $PASS  Failed: $FAIL"
[ "$FAIL" -eq 0 ]
