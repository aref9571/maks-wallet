# ADR-0005: Short-lived access token and refresh token in an HttpOnly cookie

## Status

Accepted

## Context

After login the backend must recognise the user on every later request. HTTP forgets everything between requests, so the client has to send proof of identity each time. The browser app can be attacked in two main ways: injected JavaScript (XSS) can read anything stored where scripts can reach, and another website can make the browser send cookies automatically (CSRF). A token that cannot be taken back is dangerous if it lives for a long time.

## Decision

Login returns two tokens.

- The access token is a JWT signed with HS256 that lives for 15 minutes. The frontend keeps it in memory only and sends it as `Authorization: Bearer <token>`.
- The refresh token is a random value that lives for 7 days. It is sent in a cookie with `HttpOnly`, `Secure`, `SameSite=Strict` and a path limited to the auth endpoints. The database stores only its hash.
- Every use of a refresh token rotates it: the server returns a new one and marks the old one as used. If a used token appears again, the server cancels the whole family and the user must log in again.

## Consequences

- A stolen access token works for at most 15 minutes.
- Injected scripts cannot read the refresh token because of `HttpOnly`, and other websites cannot send it because of `SameSite=Strict`.
- A leaked database does not leak usable refresh tokens, because only hashes are stored.
- Stealing and reusing a refresh token is detected by rotation.
- The frontend must refresh the access token before it expires and keep it in memory, so a page reload needs one refresh call.
- The backend needs a table for refresh token hashes and the logic for rotation and reuse detection, which is more work than a plain session.
- Rejected: a long-lived JWT in `localStorage`, because XSS can steal it and it cannot be revoked. Rejected: server sessions, which work well but teach less about tokens and need a shared session store when several services check logins.
- HS256 is enough while only the backend signs and checks tokens. If another service has to verify tokens itself, RS256 with a public key is the better choice.
