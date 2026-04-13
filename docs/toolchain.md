# Toolchain contract

## Native commands
- `./gradlew --no-daemon ktlintCheck build test`
- `./gradlew --no-daemon ktlintCheck`
- `./gradlew --no-daemon build`
- `./gradlew --no-daemon test`

## Docker commands
- `docker build -t kotlin-stakeholder .`
- `docker run --rm kotlin-stakeholder --list-values`
- `docker run --rm kotlin-stakeholder --focus-family code_analyzer --output-format json --seed docker-code`
- `docker run --rm kotlin-stakeholder --focus-family delivery_preview_ops --output-format json --seed docker-delivery`

## CI workflows
- `ci-native`
- `docker-smoke`
- `actionlint`
- `dependency-review`

## Local checks
- `python3 scripts/validate_scaffold.py`
- `nix run .#check` once Nix is available locally

## Nix installation note
- `nix` is not installed locally yet; the approved path on macOS is the official multi-user installer, not Homebrew.

## Current limitation
- `flake.lock` has not been generated locally because `nix` is not installed in the current environment.
