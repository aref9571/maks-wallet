# API overview

The Maks backend is a REST API. The web app uses it to manage users, accounts, top-ups and transfers, and admins use it to review transfers and freeze accounts.

## Conventions

- Every path starts with `/api`, and request and response bodies are JSON with `camelCase` field names.
- Money is a string with two decimals plus a currency, for example `"amount": "12.50"` and `"currency": "EUR"`.
- Times are ISO-8601 in UTC, for example `2026-11-02T10:15:30Z`, and IDs are UUIDs.
- Creating something returns `201` with the new resource and a `Location` header.
- `POST` requests that move money need an `Idempotency-Key` header, so a retry never runs twice.

## Endpoints

| # | Method | Path | Who can call it | What it does |
|---|---|---|---|---|
| A1 | POST | /api/auth/register | anyone | Creates a user and their first account, "Main" |
| A2 | POST | /api/auth/login | anyone | Logs in, returns an access token and sets the refresh cookie |
| A3 | POST | /api/auth/refresh | refresh cookie + `X-Requested-With` | Returns a new access token and a new refresh cookie |
| A4 | POST | /api/auth/logout | refresh cookie + `X-Requested-With` | Ends the session and clears the cookie |
| U1 | GET | /api/users/me | USER or ADMIN | Shows the logged-in user's profile |
| C1 | GET | /api/accounts | USER | Lists my accounts with balances |
| C2 | POST | /api/accounts | USER | Opens a new account |
| C3 | GET | /api/accounts/{accountId} | USER | Shows one of my accounts |
| C4 | PATCH | /api/accounts/{accountId} | USER | Renames one of my accounts |
| C5 | GET | /api/accounts/{accountId}/entries | USER | Shows an account's history, page by page |
| C6 | POST | /api/accounts/{accountId}/top-ups | USER + `Idempotency-Key` | Adds money with a simulated card payment |
| C7 | GET | /api/accounts/lookup?accountNumber=... | USER | Shows the masked owner name of an account before a transfer |
| T1 | POST | /api/transfers | USER + `Idempotency-Key` | Sends money to another account |
| T2 | GET | /api/transfers | USER | Lists my sent and received transfers |
| T3 | GET | /api/transfers/{transferId} | USER | Shows one transfer I sent or received |
| R1 | GET | /api/admin/transfers | ADMIN | Lists transfers waiting for review, oldest first |
| R2 | GET | /api/admin/transfers/{transferId} | ADMIN | Shows full transfer details with risk reasons |
| R3 | POST | /api/admin/transfers/{transferId}/approve | ADMIN | Approves a transfer under review |
| R4 | POST | /api/admin/transfers/{transferId}/reject | ADMIN | Rejects a transfer under review, with a note |
| R5 | GET | /api/admin/accounts?accountNumber=... | ADMIN | Finds an account and its owner |
| R6 | POST | /api/admin/accounts/{accountId}/freeze | ADMIN | Freezes an account |
| R7 | POST | /api/admin/accounts/{accountId}/unfreeze | ADMIN | Unfreezes an account |
| O1 | GET | /actuator/health | anyone | Health check for Docker and monitoring |
| O2 | GET | /api/docs | anyone | OpenAPI description and Swagger UI |
| O3 | GET | /api/status | anyone | Returns `UP` and the running version |

## Errors

Errors use the Problem Details format (RFC 9457) with `Content-Type: application/problem+json`. Each error has a `type`, `title`, `status`, `detail`, a Maks `code` such as `INSUFFICIENT_FUNDS`, and a `requestId` that links it to the server logs. Validation errors also list each wrong field in `errors`. Stack traces and SQL messages are never sent to the client.
