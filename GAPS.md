> [!NOTE]
> Missing or deferred behavior must fail fast and be tracked explicitly. No placeholder behavior should mask absent parity work.

# Kotlin Gaps

## Current explicit gaps
- `kotlin-stakeholder.ai-governance-fallback`: AI-governance families still use grouped fallback renderers.
- `kotlin-stakeholder.security-blockchain-fallback`: security/blockchain families still use grouped fallback renderers.
- `kotlin-stakeholder.health-protocol-fallback`: health/protocol families still use grouped fallback renderers.
- `kotlin-stakeholder.overlay-quantum-fallback`: overlay/quantum families still use grouped fallback renderers.
- `kotlin-stakeholder.live-provider-pending`: experimental provider flags are parsed and fail fast; live-provider integration is not implemented.
- `kotlin-stakeholder.github-required-check-binding-pending`: exact required GitHub checks stay deferred until the repo has a remote and stable CI contexts.
- `kotlin-stakeholder.flake-lock-pending`: `flake.nix` is present, but `flake.lock` was not generated because `nix` is unavailable in the current local environment.
- `kotlin-stakeholder.publication-hold`: the repo remains local-only until the program-wide 10-full-rewrites publication threshold is met.

## Guardrail
- This repo must not be pushed or published until the program-level guardrail of 10 new full rewrites with tests is met.
