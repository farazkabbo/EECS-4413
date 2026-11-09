# AI usage log

Course rule: at most **30% of the code** may be AI-generated. Every member logs every use here. The D2 and D3 reports quote this file.

| Date | Member | Tool | Use case / component | Prompt (summary) | Files generated | Human edits | Reviewer | Generated / total lines |
|---|---|---|---|---|---|---|---|---|
| 2026-11-08 | Mohammed | Claude Code | Setup, M17 DAL, M3 controllers, M18 schema, hot deals, project skeleton | "Create the folder structure separating frontend and backend, and do Mohammed's part (schema, DAOs, REST controllers with validation, hot deals, deployment)" | backend/ skeleton, schema.sql, data.sql, domain/, dao/, controller/, exception/, service stubs, frontend/ skeleton, Dockerfile, render.yaml, tests/curl | _to fill_ | _to fill_ | _to count_ |

How to count lines: `git diff --stat` on the AI-generated commit, minus the lines a human rewrote afterwards.
