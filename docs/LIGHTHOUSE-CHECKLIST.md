# Lighthouse running check list

Updated 2 November 2026. **Comprehensive check: waiting for the user's instruction.**

Add checks as features change. Record the build, device, date, evidence and result for each check.
Use passed, failed, skipped or unverified; a prior pass is historical evidence, not a guarantee about
later builds. Keep a record of issues for the final report. Raise an immediate blocker when it
prevents useful progress or requires the user's decision; otherwise save the report for the big check.
No build, test suite or TV interaction is required just to maintain this list.

## Viewing session

- [ ] Avoid interrupting the user's viewing for routine tests; use local checks where sufficient.
- [ ] Before a genuinely necessary interruption, capture the foreground app, title/episode and
      playback position/resume state where accessible, without changing playback.
- [ ] Establish a reliable return path first. If content or position cannot be recovered, obtain
      the missing information before interrupting. Do not assume an app launch restores the session.
- [ ] Keep any necessary interruption brief; stop the Lighthouse test afterwards.
- [ ] Return to the same content and position, restore playback and verify the result.
- [ ] Preserve the TV's existing default screensaver and unrelated apps/settings.

## Pending comprehensive check

- [ ] Install and verify lighthouse.3 (150) when authorized for the final batch; the installed .2
      APK and its historical test results do not contain the new rain/caption changes.
- [ ] Rain: confirm hourly probabilities arrive on the device; verify peak chance, the first
      contiguous likely window, threshold, midnight/tomorrow labels and API offset handling.
- [ ] Rain: check zero versus missing/invalid/partial/stale hours, failure recovery and refresh;
      inspect three/four-line weather layout for overlap and legibility with the top-right clock.
- [ ] Captions: inspect Apple, Amazon and both community sources/quality variants; city/state and
      country, broad country-only locations, missing/ambiguous locations, and orbital clips.
- [ ] Captions: check custom/local/network media with and without structured location data;
      confirm original metadata is intact, dynamic narration no longer appears for description/POI
      selection, and explicit filename/title/location selections and photos behave as before.
- [ ] Record exact source revision, APK hash/version/signature, device and Android version.
- [ ] Run the relevant custom unit tests, Kotlin formatting/lint and debug build; inspect changes.
- [ ] Check upstream-flavor compatibility for shared edits and retained licence/attribution.
- [ ] Check Jost across Main, Settings, submenus, dialogs, loading/error states and overlays;
      inspect weight/italic mapping, legibility, focus and long text on the TV.
- [ ] Check top-right clock/day/date, local timezone, 12/24-hour preference and date rollover.
- [ ] Check top-left Umhlanga Rocks weather, conditions/icon, units and sunrise/sunset dates;
      inspect overlap, contrast and sizing on bright/dark video.
- [ ] Observe the real 20-minute weather refresh, five-minute failure retry and recovery.
- [ ] Exercise unavailable/offline/server-error data and ensure old data is not presented as current.
- [ ] Check location search, direct coordinates, timezone changes and solar-date rollover.
- [ ] Check fresh-install defaults, one-time upgrade migration, retained preferences and subsequent
      user layout edits; check coexistence with original Aerial Views.
- [ ] Check D-pad focus/navigation, Back, preview start/exit, skip/pause/seek and video transitions.
- [ ] Check actual system-managed DreamService idle start, wake, repeated activation and reboot
      behavior. Any change to the default screensaver must be separately agreed and restored.
- [ ] Check sustained SDR playback, HDR/Dolby Vision and refresh-rate switching on compatible media.
- [ ] Check local/USB/network sources, file/notification permissions and integrations actually used.
- [ ] Investigate the earlier debug retained-object diagnostic; compare lifecycle/resource recovery
      after repeated starts/exits and a sustained/overnight run.
- [ ] Check native Android Studio sync/build in the Lighthouse project window only.
- [ ] When release signing is configured, check release/R8 packaging, signature and signed upgrade
      with preferences retained. Never use upstream signing credentials.
- [ ] Record final failures and remaining unverified items separately, then restore viewing.

## Outstanding findings and limits

- An earlier debug preview exit reported **four retained objects**. Cause and impact remain
  unresolved; this is a diagnostic finding, not a confirmed memory leak or a claimed fix.
- Timed refresh/retry, forced network failures, real idle DreamService activation, HDR/Dolby Vision,
  overnight stability and native Studio sync remain unverified in the latest update.
- Release signing is not configured. Release packaging and signed upgrades are pending.
- Remote CI is not enabled; `lighthouse-checks.yml.example` is a template, not an active workflow.

## Historical evidence

The existing report `../../Lighthouse-Jost-weather-verification.md` records the checks already
performed on 2 November 2026 for **1.8.5-lighthouse.2 (149)**, source
`997292de2c0dc8f2b8c5a83879923255d0c64330`: 316 custom unit tests and 304 upstream-flavor tests
passed; formatting, lint, debug packaging and APK integrity/signature checks passed. Installation,
font/overlay rendering, network weather fetch, preview playback and D-pad skip were observed on
the Android 14 TCL Smart TV Pro. The default screensaver remained unchanged.

These checks preceded the user's request to defer comprehensive testing. No new device check has
been performed to create this list. Preserve those results separately from the eventual batch report.

## Final batch results

Not run. Await the user's instruction, then record results and evidence here.

## Targeted local implementation checks — lighthouse.3

2 November 2026: formatter and compilation for the selected unit-test task completed successfully.
Only `RainOutlookTest`, `MeteoMapperTest`, `LighthouseCaptionsTest` and `MetadataResolverTest` were
run: **38 passed, zero failed/errors/skipped**. This covers hourly-window alignment, midnight,
missing/invalid data, caption formatting and the metadata callback/fallback path.
No full suite, new APK assembly, installation, live weather request or TV interaction was performed
for this update. Visual rendering and the comprehensive checks above remain pending.
