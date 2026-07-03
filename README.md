# TawagCheck

A privacy-first scam call detector for Philippine Android users. TawagCheck screens incoming calls locally using Android's `CallScreeningService`, checks the number against a local scam database and a set of heuristics, and warns, silences, or rejects the call before the phone rings — all on-device. There is no caller-name lookup service and no analytics; the only network call in the app is a manual, user-triggered scam-list update.

## Features (Phase 1)

- **Call screening** via `RoleManager`-granted `ROLE_CALL_SCREENING`, with a Taglish/English onboarding flow explaining why.
- **Local scam database** (Room): full/prefix number matches against `smishing` / `fake_bank` / `spoofed` / `user_reported` categories, seeded from a bundled placeholder list, updatable only via a manual "Update database" button pointed at a `scamlist.json` URL (defaults to this repo's GitHub Releases).
- **Heuristics engine**: scores each call 0–100 from database matches, international-number-masquerading-as-local detection (via `libphonenumber`), and optional not-in-contacts + repeated-call detection (degrades gracefully without `READ_CONTACTS`).
- **Post-call verdict + history**: a notification with Block / Report as scam / Ignore actions, plus a filterable call history screen.
- **Dashboard**: protection toggle, calls-screened-today and cumulative-scams-blocked counters, and a 7-day flagged-calls chart (Vico).
- **Settings**: per-risk-tier actions (allow/warn/silence/reject), hidden-number policy, contacts permission toggle, database update URL, English/Taglish language toggle, and CSV export of call history via the system share sheet.

## Tech stack

Kotlin, Jetpack Compose, MVVM with a small hand-rolled `AppContainer` service locator (no DI framework), Room, DataStore Preferences, Navigation Compose, kotlinx.serialization, OkHttp (manual update fetch only), Google `libphonenumber`, Vico for charts. `minSdk 29` (Android 10, required for `CallScreeningService`/`RoleManager.ROLE_CALL_SCREENING`), `compileSdk`/`targetSdk 35`.

## Known Android platform limitations

- `CallScreeningService.onScreenCall` is never invoked at all for calls with `RESTRICTED`/`UNKNOWN`/`UNAVAILABLE`/`PAYPHONE` handle presentation (confirmed against the AOSP source) — this is a framework-level filter, not something a third-party screening app can override. The hidden-number policy in Settings only applies to the rarer case of a blank/empty handle that still reaches the service.
- There's no "call disconnected" callback available to a `CallScreeningService` without `READ_PHONE_STATE`, which was deliberately excluded to keep permissions minimal. Verdict notifications fire at screening time (when the call arrives) rather than after it hangs up.

## Building

```
./gradlew assembleDebug     # debug build
./gradlew assembleRelease -PversionName=1.0.0 -PversionCode=10000   # release build
```

Release APKs are built and published automatically by `.github/workflows/release.yml` whenever a `v*` tag is pushed (e.g. `v1.0.0`), producing `TawagCheck-vX.X.X.apk` attached to a GitHub Release. The release build type is currently debug-signed (no release keystore configured yet) since Phase 1 targets direct sideloading rather than the Play Store.

## Project structure

```
app/src/main/java/com/tawagcheck/app/
  data/        # Room DB, DataStore, repositories, remote update service, models
  domain/      # phone number normalization, heuristics engine
  service/     # CallScreeningService, RoleManager helper
  notification/# post-call verdict notifications
  ui/          # Compose screens, ViewModels, theme, EN/Taglish strings
  util/        # CSV export
.claude/agents/
  verify-implementation.md   # review gate run before any release build
  allow-once-approver.md     # auto-approves Android permission dialogs during device testing
```

## Out of scope (Phase 2)

- Community scam list sync + report submission backend
- SMS scam filtering
