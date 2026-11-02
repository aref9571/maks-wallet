# Contributing

## Branches

- `main` always holds working, tested code.
- Every piece of work happens on its own branch named `day-NN-short-topic`, for example `day-12-jpa-users`.
- A branch starts from an up-to-date `main`:

```bash
git switch main
git pull --ff-only origin main
git switch -c day-NN-short-topic
```

## Commit messages

Commits follow [Conventional Commits](https://www.conventionalcommits.org): `type(scope): summary`.

- Types: `feat`, `fix`, `test`, `refactor`, `docs`, `style`, `build`, `ci`, `chore`.
- Scopes: `backend`, `risk`, `frontend`, `e2e`, `infra`, `docs`.
- The summary uses the imperative mood ("add", not "added"), and the whole line is at most 72 characters.
- One logical change per commit, and every commit builds.

Example: `feat(backend): add user entity and repository`

## Getting changes onto main

`main` only moves by fast-forward, so its history stays a straight line with no merge commits.

1. Push the branch and wait for the checks to pass.
2. If `main` moved in the meantime, rebase the branch on `main` and run the tests again.
3. Fast-forward `main` and push it:

```bash
git switch main
git pull --ff-only origin main
git merge --ff-only day-NN-short-topic
git push origin main
```

4. Delete the branch locally and on GitHub.
