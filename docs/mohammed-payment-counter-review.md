# Payment Counter Schema Review

Owner: Zaaid
Reviewer: Mohammed
Status: Pending Mohammed approval

## Change

The mock payment gateway uses one database row to keep the deterministic attempt sequence consistent across requests and application instances:

```sql
CREATE TABLE payment_attempt_counter (
    id BIGINT PRIMARY KEY,
    attempts BIGINT NOT NULL DEFAULT 0 CHECK (attempts >= 0)
);

INSERT INTO payment_attempt_counter (id, attempts) VALUES (1, 0);
```

The DAO reads this row with a pessimistic write lock. The gateway increments the value only for an actual authorization call. Attempts 1, 2, 4, and 5 approve; attempts 3 and 6 deny, repeating thereafter.

## Supabase verification

Project verification completed on 2026-11-09 for Supabase project `fritz-ev` (`krzzrvhuwbqxrntbluhu`). The repository schema and seed data were applied to the previously empty project. The verification query returned the expected payment-counter row, and the core seed counts were confirmed:

```text
users=4, vehicles=14, customization_options=14, orders=4, reviews=3,
payment_attempt_counter rows=1, attempts=0
```

The local backend was then started with the `supabase` profile and `DB_INIT_MODE=never`. On 2026-11-09, `/actuator/health` responded successfully; read-only smoke checks returned the seeded vehicle catalogue, review listing, authenticated `alice` session, and cart retrieval.

## Local Supabase test account

- `mock_zaaid` was added as a customer account for local verification.
- The account has billing and shipping test addresses and is not a verified owner or administrator.
- PostgreSQL identity sequences for the seeded `users` and `addresses` rows were aligned before insertion.
- Local HTTP sessions use `SESSION_COOKIE_SAME_SITE=lax` and `SESSION_COOKIE_SECURE=false`; production HTTPS should retain `SameSite=None` and `Secure`.

Run the following query to recheck the counter:

```sql
SELECT id, attempts
FROM payment_attempt_counter
ORDER BY id;
```

Expected initial result: `1 | 0`.

## Approval checklist

- [ ] Table definition is compatible with the team-owned schema.
- [ ] Seed row `(1, 0)` is present.
- [ ] Pessimistic locking is acceptable for Supabase PostgreSQL.
- [ ] `schema.sql` remains the single schema owner.
- [x] Supabase schema has been applied and verified on 2026-11-09.
- [ ] Mohammed has approved the change in the team channel or pull request.
