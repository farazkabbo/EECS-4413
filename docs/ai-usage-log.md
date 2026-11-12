# AI usage log

Course rule: at most **30% of the code** may be AI-generated. Every member logs every use here. The D2 and D3 reports quote this file.

| Date | Member | Tool | Use case / component | Prompt (summary) | Files generated | Human edits | Reviewer | Generated / total lines |
|---|---|---|---|---|---|---|---|---|
| 2026-11-08 | Mohammed | Claude Code | Setup, M17 DAL, M3 controllers, M18 schema, hot deals, project skeleton | "Create the folder structure separating frontend and backend, and do Mohammed's part (schema, DAOs, REST controllers with validation, hot deals, deployment)" | backend/ skeleton, schema.sql, data.sql, domain/, dao/, controller/, exception/, service stubs, frontend/ skeleton, Dockerfile, render.yaml, tests/curl | _to fill_ | _to fill_ | _to count_ |
| 2026-11-09 | Zaaid | Codex | M4 account, M6 pricing integration, M7 cart, M8 checkout, M12 reviews, M15 mock payment, CSRF, D2 pages, React/Tailwind flows, tests | "Implement the approved Fritz EV Zaaid scope plan against the existing repository, preserving team contracts and reporting tests truthfully" | backend account/cart/configurator/order/review/payment/security classes, schema payment counter, static D2 pages, frontend auth/cart/checkout/detail pages, ZaaidServiceTests | Review and verification performed with backend Maven tests and frontend production build | Pending team review | See final diff and test commands |
| 2026-11-11 | Ronak | Claude (claude.ai chat) | M11 test drives, M14 analytics and dashboard, JUnit tests, curl script, D2 pages | "Walk me through implementing getDashboardSnapshot, doubleBookingIsRejected,Session and CSRF plumbing, explain with great detail so I get a deeper understanding"; | getDashboardSnapshot and helpers; duplicate-key guard in TestDriveServiceImpl.book(); doubleBookingIsRejected test; csrf_token/login_as in api-tests.sh | I wrote everything else in my files by hand (and research), ran all tests locally, and checked both pages in the browser. | Pending team review | to-do |

How to count lines: `git diff --stat` on the AI-generated commit, minus the lines a human rewrote afterwards.

Instructor AI exception status: the repository specification records a student-reported allowance for up to 100% AI-generated implementation as of 2026-11-08. Authentic instructor evidence was not present in the repository during this implementation; the written course/D2 rule remains recorded as 30% until the team attaches genuine confirmation.
