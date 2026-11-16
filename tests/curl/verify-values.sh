#!/usr/bin/env bash
# =====================================================================
# Fritz EV - value checks for the test-drive and analytics APIs (Ronak).
# Unlike api-tests.sh (status codes), this checks the CONTENT of responses
# against numbers counted by hand from backend/src/main/resources/data.sql.
#
# Usage:  bash tests/curl/verify-values.sh            (run from the repo root)
# Needs:  bash + curl, and a FRESHLY STARTED back end (cd backend && ./mvnw spring-boot:run).
#         The seed counts below only hold on a fresh start (H2 resets on restart).
# =====================================================================
BASE="${BASE_URL:-http://localhost:8080}"
J="$(mktemp)"
PASS=0; FAIL=0

tok() { curl -s -b "$J" -c "$J" "$BASE/api/auth/csrf" | sed 's/.*"token":"\([^"]*\)".*/\1/'; }
login() {   # login <username> <password>
  local t; t=$(tok)
  curl -s -o /dev/null -b "$J" -c "$J" -X POST "$BASE/api/auth/login" \
    -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $t" \
    -d "{\"username\":\"$1\",\"password\":\"$2\"}"
}
expect() {  # expect <label> <response> <grep pattern>
  if echo "$2" | grep -q "$3"; then PASS=$((PASS+1)); echo "PASS $1"
  else FAIL=$((FAIL+1)); echo "FAIL $1 -> $2"; fi
}

echo "== Dashboard values (seed data: 9 VIEW, 3 CART, 2 PURCHASE) =="
login admin 'Admin123!'
D1=$(curl -s -b "$J" "$BASE/api/admin/dashboard")
expect "funnel VIEW = 9"            "$D1" '"VIEW":9'
expect "funnel CART = 3"            "$D1" '"CART":3'
expect "funnel PURCHASE = 2"        "$D1" '"PURCHASE":2'
expect "demand IONIQ 5 = 7"         "$D1" '"Hyundai IONIQ 5":7'
expect "demand Polestar 2 = 6"      "$D1" '"Polestar 2":6'
expect "battery health Model Y = 96" "$D1" '"2024 Tesla Model Y":96'

echo "== Metrics update after a new event =="
login alice 'Password123!'
curl -s -o /dev/null -b "$J" -c "$J" -X POST "$BASE/api/events" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $(tok)" \
  -d '{"eventType":"VIEW","vehicleId":1}'
login admin 'Admin123!'
D2=$(curl -s -b "$J" "$BASE/api/admin/dashboard")
expect "funnel VIEW rises to 10"    "$D2" '"VIEW":10'

echo "== Test-drive booking lifecycle =="
login alice 'Password123!'
B=$(curl -s -b "$J" -c "$J" -X POST "$BASE/api/test-drives" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $(tok)" -d '{"slotId":3}')
expect "booking returns slotId 3"   "$B" '"slotId":3'
expect "mine lists the booking"     "$(curl -s -b "$J" "$BASE/api/test-drives/mine")" '"slotId":3'
ID=$(echo "$B" | sed 's/.*"id":\([0-9]*\).*/\1/')
curl -s -o /dev/null -b "$J" -c "$J" -X DELETE "$BASE/api/test-drives/$ID" -H "X-XSRF-TOKEN: $(tok)"
expect "mine empty after cancel"    "$(curl -s -b "$J" "$BASE/api/test-drives/mine")" '^\[\]$'

rm -f "$J"
echo
echo "Passed: $PASS  Failed: $FAIL"
[ "$FAIL" -eq 0 ]