> [!IMPORTANT]
> This repository is part of a Codex-assisted rewrite experiment. All changes are manually reviewed, a human remains in the loop, and missing behavior is tracked explicitly rather than hidden. The project exists for fun, research, language learning, AI agent workflow/planning, interop experiments, and code review testing.
# kotlin-stakeholder

Kotlin parity target under `stakeholder-circus`.

## Status
- Active local implementation tranche.
- Imported Rust history is preserved for attribution and auditability.
- Governance, provenance, hook, and CI baselines remain in place.
- Classic-six and modern-core are implemented locally with deterministic normalized JSON and explicit experimental-provider fail-fast behavior.
- Native and Docker validation now pass locally.
- This repo remains local-only and is not for push until the program-wide 10-full-rewrites publication guardrail is met.
- The next queue tip in the program docs is `elixir-stakeholder`.

## Role
- Null-safe JVM parity sibling target.
- Purpose: JVM parity contrast to Java using null-safety and sealed hierarchies while preserving the CLI schema exactly.
- Program category: ecosystem reach, interop

## Commands
- `./gradlew --no-daemon ktlintCheck build test`
- `docker build -t kotlin-stakeholder .`
- `docker run --rm kotlin-stakeholder --list-values`
- `docker build -t kotlin-stakeholder .`
- `docker run --rm kotlin-stakeholder --list-values`

## Current guardrail
- This repo is intentionally local-only until the program-wide 10-full-rewrites publication threshold is met.
- Later packet families remain grouped fallback with explicit gaps until their dedicated tranche lands.

## Documentation
- [AI disclosure](AI_DISCLOSURE.md)
- [Parity](PARITY.md)
- [Explicit gaps](GAPS.md)
- [Remotes](docs/remotes.md)
- [Provenance](docs/provenance.md)
- [Toolchain](docs/toolchain.md)
- [Traceability](docs/traceability/first-push-families.md)
