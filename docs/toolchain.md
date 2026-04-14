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
- `nix run .#check`

## Nix installation note
- `nix` is installed locally through the official multi-user installer on macOS.

## Current limitation
- `flake.lock` is now generated locally through the installed Nix toolchain.
