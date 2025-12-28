# Lineage Source Decision Matrix

Purpose
-------
A practical gate to decide whether a lineage source is ready for ingestion into the pipeline or needs transformation/normalization first. Use this as the authoritative checklist during onboarding or review of a new source.

How to use
----------
1. Walk through the Must-have items first — any failure here means the source is NOT ready and needs transformation.
2. Evaluate Recommended items — they are high value but not strictly blocking.
3. If any Must-have fails, mark the source as "Reject / Transform" and record required fixes.
4. If Must-have passes but several Recommended items are missing, mark "Ready with caveats" and create follow-up tasks.

Decision matrix (quick)
-----------------------
- Ready: All Must-have items ✅ and core validation tests pass.
- Ready with caveats: Must-have ✅, some Recommended ❌; acceptable for a controlled rollout.
- Reject / Transform: Any Must-have ❌.

Must-have (blockers)
---------------------
- Contract & wiring
  - Implements `LineageSource` and is registered as a Spring bean (e.g., `@Component`).
  - `sourceName()` returns a unique, human-readable id.
  - `ingestRaw()` returns `List<Map<String,Object>>`.

- Record shape
  - Each record contains a `name` key of type `String` (non-empty).
  - Relation keys use the expected names: `readsFrom`, `writesTo`, `publishesTo`, `consumesFrom`.

- Relation types
  - Relation values are `List<String>` or `null`. Single strings must be coerced to `List<String>`.

- Null-safety
  - `ingestRaw()` returns a `List` (empty if no records), never `null`.

- Security
  - No secrets or credentials hard-coded in source code; configuration values live in `application.yml`, env vars, or a secrets manager.

Recommended (non-blocking but strongly encouraged)
--------------------------------------------------
- Naming & normalization
  - Trim whitespace, normalize casing per project policy, and deduplicate relation entries.
  - Conform to persistence nodeId patterns (e.g., `application:<name>:prod`, `data:<name>:prod`).

- Idempotency
  - Stable identifiers or idempotent ingestion so repeated runs don't create duplicates or inconsistent state.

- Error handling & resilience
  - Timeouts, retries with exponential backoff for remote calls.
  - Clear behavior for partial failures (documented): return partial results or empty list, and log errors.

- Performance
  - Support pagination/batching for large datasets; avoid returning huge in-memory lists.

- Observability
  - Logs for start/finish, record counts, and errors.
  - Metrics: processed count, successes, failures, latency.

- Configuration & controls
  - Tunables (timeouts, page sizes, feature flag) exposed in configuration.
  - Feature flag / enable switch for safe rollout and rollback.

- Tests
  - Unit tests: happy path, missing `name`, non-list relations, empty result.
  - Integration test: aggregator + persistence using mocks or test harness.

Minimal validation (can be automated)
------------------------------------
For each record:
- Validate `name`:
  - Must exist and be a `String` and not empty.
  - Normalize (trim, apply case rules).
- For each relation key (`readsFrom`, `writesTo`, `publishesTo`, `consumesFrom`):
  - If value == `null` -> OK.
  - Else if value instanceof `List` -> ensure all elements are `String` (coerce or reject non-strings).
  - Else if value instanceof `String` -> convert to single-element list.
  - Else -> reject or transform.
- Deduplicate relation lists and trim entries.
- Return a validated/normalized list (never `null`).

Quick validation pseudocode
--------------------------
1. If `name` missing or not string -> fail record.
2. Normalize `name` -> `name = normalize(name)`.
3. For relationKey in relationKeys:
   - val = record.get(relationKey)
   - if val == null -> continue
   - else if val instanceof String -> record.put(relationKey, List.of((String)val))
   - else if val instanceof List -> coerce elements to String and remove non-strings
   - else -> mark record invalid
4. If record valid -> add to output; else -> log and optionally collect for manual review.

Example quick-pass criteria
---------------------------
- Every record has `name` and at least one relation or explicit justification for no relations.
- All relations are lists of strings after normalization.
- Output size is within expected operational limits or the source supports pagination.

When to mark a source as "Needs transformation"
-------------------------------------------------
- Records missing `name` or `name` not a string.
- Relation fields are heterogeneous or contain non-string values that can't be safely coerced.
- Names are inconsistent across environments and require mapping (e.g., `billing-prod` vs `billing:prod`).
- Very large datasets without batching support.
- Sensitive fields present that must be stripped.
- Source is unstable and causes frequent transient failures without retry/backoff.

Operational notes
-----------------
- Use a config flag (e.g., `lineage.sources.<sourceName>.enabled`) to gate ingestion.
- Prefer dry-run mode for initial ingestion to validate behaviour without persisting.
- Maintain a changelog for transformations applied to source data so audits can trace decisions.

Appendix: Example decision flow (one-paragraph)
-----------------------------------------------
Run automated validators: contract check, `name` presence/type, relation coercion, and normalization. If any Must-have fails, block and request fixes. If Must-have passes, run ingestion in dry-run mode, inspect sample transformed output and metrics, then enable in a controlled rollout with feature flag and monitoring. Schedule follow-up work for any Recommended items not yet implemented.


---
Generated by project maintainers' checklist; copy into PR templates or onboarding playbooks as needed.

