# ADR-0011: CSRF protection is disabled for the API

## Status

Accepted

## Context

Spring Security turns CSRF protection on by default. It protects applications that
rely on cookies, because a browser attaches cookies to every request automatically,
even when the request was started from another website.

The Maks API is stateless. The access token travels in the `Authorization` header,
which a browser never attaches by itself, so a foreign website cannot make the
browser send it. The only endpoints that use a cookie are the refresh and logout
endpoints, which hold the refresh token.

## Decision

CSRF protection is disabled in `SecurityConfig` for the whole API, which uses
stateless sessions and no form login or HTTP Basic.

## Consequences

Clients do not need to fetch or send CSRF tokens, and requests with only a header
token work without extra steps.

The cookie endpoints still need their own protection: the refresh cookie uses
`SameSite=Strict`, the endpoints accept only same-origin requests, and they require
the `X-Requested-With` header.

Rejected alternative: keeping CSRF on and sending a token with every request. It adds
work for every client and gives no extra safety for header-based tokens.

If the API ever authenticates normal endpoints with a cookie, this decision must be
reviewed.
