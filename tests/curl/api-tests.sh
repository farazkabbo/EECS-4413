#!/usr/bin/env bash 
# ===================================================================== 
# Fritz EV - curl test script for the back-end REST API. 
# Owner: Ronak (PB-33). Covers catalogue, hot deals, validation, admin 
# guard, test drives, event tracking and admin reports/dashboard. 
# Zaaid's section remains TODO at the bottom.
# 
# Usage:   ./tests/curl/api-tests.sh                    (local, http://localhost:8080) 
#          BASE_URL=https://fritz-ev-api.onrender.com ./tests/curl/api-tests.sh 
# Needs:   bash + curl. Start the back end first (cd backend && ./mvnw spring-boot:run). 
#          Run against a freshly started back end: the script books and cancels slot 3. 
# ===================================================================== 
BASE_URL="${BASE_URL:-http://localhost:8080}" 
COOKIES="$(mktemp)" 
PASS=0; FAIL=0 
TOKEN="" 

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

check_body_contains() {
  local desc="$1" expected="$2"
  if grep -Fq -- "$expected" /tmp/fritz_body.json; then
    PASS=$((PASS+1)); echo "PASS [body] $desc"
  else
    FAIL=$((FAIL+1)); echo "FAIL [body] $desc"
    head -c 300 /tmp/fritz_body.json; echo
  fi
}

# --- helpers for authenticated / state-changing calls (CSRF token + session cookie) --- 
csrf_token() { 
  TOKEN=$(curl -s -b "$COOKIES" -c "$COOKIES" "$BASE_URL/api/auth/csrf" | sed 's/.*"token":"\([^"]*\)".*/\1/') 
} 
login_as() {   # login_as <username> <password> 
  csrf_token 
  curl -s -o /dev/null -b "$COOKIES" -c "$COOKIES" -X POST "$BASE_URL/api/auth/login" \ 
    -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" \ 
    -d "{\"username\":\"$1\",\"password\":\"$2\"}" 
  csrf_token 
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
csrf_token 
check "malformed JSON -> 400"                               400 -X POST "$BASE_URL/api/calculators/loan" -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{oops' 
check "invalid loan fields -> 400"                          400 -X POST "$BASE_URL/api/calculators/loan" -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"price":-1,"months":0}' 

echo "== Admin guard (T12) ==" 
check "sales report without login -> 401"                   401 "$BASE_URL/api/admin/reports/sales?year=2026&month=10" 
check "dashboard without login -> 401"                      401 "$BASE_URL/api/admin/dashboard" 

echo "== Test drives (UC-11, Ronak) ==" 
TD_DATE=$(date -v+2d +%F 2>/dev/null || date -d '+2 days' +%F) 
check "free at-home slots"                                  200 "$BASE_URL/api/vehicles/1/test-drive-slots?type=AT_HOME&date=$TD_DATE" 
check "invalid drive type -> 400"                           400 "$BASE_URL/api/vehicles/1/test-drive-slots?type=BOAT&date=$TD_DATE" 
csrf_token 
check "booking while signed out -> 401"                     401 -X POST "$BASE_URL/api/test-drives" -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"slotId":3}' 
login_as alice 'Password123!' 
check "at-home booking without address -> 400"              400 -X POST "$BASE_URL/api/test-drives" -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"slotId":1}' 
check "unknown slot -> 404"                                 404 -X POST "$BASE_URL/api/test-drives" -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"slotId":99999}' 
check "book virtual slot 3"                                 201 -X POST "$BASE_URL/api/test-drives" -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"slotId":3}' 
BOOKING_ID=$(sed 's/.*"id":\([0-9]*\).*/\1/' /tmp/fritz_body.json) 
check "double booking the same slot -> 409"                 409 -X POST "$BASE_URL/api/test-drives" -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"slotId":3}' 
check "my bookings"                                         200 "$BASE_URL/api/test-drives/mine" 
check "cancel my booking"                                   204 -X DELETE "$BASE_URL/api/test-drives/$BOOKING_ID" -H "X-XSRF-TOKEN: $TOKEN" 

echo "== Event tracking (UC-18) ==" 
check "log a VIEW event"                                    202 -X POST "$BASE_URL/api/events" -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"eventType":"VIEW","vehicleId":1}' 
check "event for unknown vehicle -> 404"                    404 -X POST "$BASE_URL/api/events" -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"eventType":"VIEW","vehicleId":99999}' 

echo "== Reports as customer / admin (UC-17..19) ==" 
Y=$(date +%Y); M=$(date +%-m) 
login_as bob 'Password123!' 
check "customer sales report -> 403"                        403 "$BASE_URL/api/admin/reports/sales?year=$Y&month=$M" 
check "customer dashboard -> 403"                           403 "$BASE_URL/api/admin/dashboard" 
login_as admin 'Admin123!' 
check "admin sales report"                                  200 "$BASE_URL/api/admin/reports/sales?year=$Y&month=$M" 
check "admin usage report"                                  200 "$BASE_URL/api/admin/reports/usage?from=$Y-01-01&to=$Y-12-31" 
check "usage report from > to -> 400"                       400 "$BASE_URL/api/admin/reports/usage?from=$Y-12-31&to=$Y-01-01" 
check "sales report invalid month -> 400"                   400 "$BASE_URL/api/admin/reports/sales?year=$Y&month=13" 
check "admin dashboard"                                     200 "$BASE_URL/api/admin/dashboard" 

# ---------------------------------------------------------------------
# Catalogue and calculators (UC-07..09, UC-20; Tamjeed)
echo "== Compare vehicles and configurator (UC-07..08) =="
check "compare two vehicles"                                200 "$BASE_URL/api/vehicles/compare?ids=1,2"
check_body_contains "comparison includes both vehicles"     '"vehicles"'
check "compare fewer than two vehicles -> 400"              400 "$BASE_URL/api/vehicles/compare?ids=1"
check "compare unknown vehicle -> 404"                      404 "$BASE_URL/api/vehicles/compare?ids=1,99999"
check "get vehicle customization options"                   200 "$BASE_URL/api/vehicles/1/options"
check_body_contains "options are grouped by category"       '"EXTERIOR_COLOUR"'
csrf_token
check "price valid configuration"                           200 -X POST "$BASE_URL/api/vehicles/1/configuration" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"optionIds":[1,6]}'
check_body_contains "configuration response includes total" '"total":54990.00'
check "selecting two options in one category -> 400"        400 -X POST "$BASE_URL/api/vehicles/1/configuration" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"optionIds":[1,2]}'
check "duplicate configuration option -> 400"               400 -X POST "$BASE_URL/api/vehicles/1/configuration" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"optionIds":[1,1]}'
check "option not offered for vehicle -> 400"                400 -X POST "$BASE_URL/api/vehicles/1/configuration" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d '{"optionIds":[5]}'

echo "== Loan and fuel-savings calculators (UC-09, UC-20) =="
check "zero-rate loan calculation"                          200 -X POST "$BASE_URL/api/calculators/loan" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" \
  -d '{"price":12000,"downPayment":0,"annualRatePercent":0,"months":12}'
check_body_contains "loan quote includes monthly payment"   '"monthlyPayment":1000.00'
check "down payment exceeds price -> 400"                   400 -X POST "$BASE_URL/api/calculators/loan" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" \
  -d '{"price":12000,"downPayment":12001,"annualRatePercent":0,"months":12}'
check "fuel-savings and CO2 estimate"                       200 -X POST "$BASE_URL/api/vehicles/1/savings" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" \
  -d '{"annualKm":20000,"gasolineLPer100km":8,"gasolinePricePerL":1.5,"electricityPricePerKwh":0.2}'
check_body_contains "savings response includes yearly savings" '"yearlySavings":1804.00'
check_body_contains "savings response includes assumptions" '"assumptions"'
check "invalid fuel consumption -> 400"                    400 -X POST "$BASE_URL/api/vehicles/1/savings" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" \
  -d '{"annualKm":20000,"gasolineLPer100km":31,"gasolinePricePerL":1.5,"electricityPricePerKwh":0.2}'
check "savings estimate for unknown vehicle -> 404"         404 -X POST "$BASE_URL/api/vehicles/99999/savings" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" \
  -d '{"annualKm":20000,"gasolineLPer100km":8,"gasolinePricePerL":1.5,"electricityPricePerKwh":0.2}'

# --------------------------------------------------------------------- 
# TODO(Zaaid):  register / login / logout, cart, checkout x3 (approve, approve, deny), reviews 
# --------------------------------------------------------------------- 

rm -f "$COOKIES" 
echo 
echo "Passed: $PASS  Failed: $FAIL" 
if [ "$FAIL" -eq 0 ]; then
  exit 0
fi
exit 1