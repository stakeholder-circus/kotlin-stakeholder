# kotlin-stakeholder AGENTS

1. Preserve imported Rust history and explicit provenance docs; do not present this repo as greenfield work.
2. This repo is in the active local rewrite tranche and remains local-only until the program-level 10-full-rewrites publication guardrail is met.
3. Validation commands:
   - `python3 scripts/validate_scaffold.py`
   - `./gradlew --no-daemon ktlintCheck`
   - `./gradlew --no-daemon build`
   - `./gradlew --no-daemon test`
   - `docker build -t kotlin-stakeholder .`
4. Keep `origin` intended for `stakeholder-circus/kotlin-stakeholder` and `upstream` pointed at `https://github.com/giacomo-b/rust-stakeholder`.
5. Implementation must keep deterministic normalized JSON, explicit experimental-provider fail-fast behavior, and traceability rows back to Rust, Java, and stakeholder-core.
6. Do not hide missing behavior behind placeholders; record it in `GAPS.md` instead.
