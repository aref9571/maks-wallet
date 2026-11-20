# ADR-0001: Monorepo

## Status

Accepted

## Context

Maks has several parts: the Spring Boot backend, the Python risk service, the React frontend, the end-to-end tests and the infrastructure files. One person builds and maintains all of them. We have to decide whether each part lives in its own repository or all parts live in one.

## Decision

All parts live in one repository, in the folders `backend/`, `risk-service/`, `frontend/`, `e2e/` and `infra/`.

## Consequences

- A change that touches more than one service, for example a new API field and the frontend that reads it, is one commit and one review.
- There is one README, one set of rules and one CI pipeline to maintain.
- One place makes the project easier to learn and to explain.
- A pipeline that runs everything can get slower as the project grows, and a small change in one folder may start checks for the others.
- Rejected: one repository per service. It gives clear borders between teams, but for one person it only adds work: several pipelines, several READMEs and changes that must be kept in step by hand.
