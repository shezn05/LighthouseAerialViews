# Lighthouse Aerial Views

Independent fork by shezn05. Initial modifications dated 2 November 2026.

## Jost and Umhlanga layout — lighthouse.2

Version `1.8.5-lighthouse.2` (149) bundles the complete original Jost static TTF family:
nine weights from 100 to 900, upright and italic. Menus, dialogs, loading text and screensaver
overlays use Jost. Font files remain unmodified; the SIL OFL 1.1 licence and source hashes are
bundled in `JOST-OFL.txt` and `JOST-SOURCE.txt` in the app assets. In this flavor, the upstream
`googlesans` resource name resolves to Jost so existing font helpers keep working.

The first launch after this update applies the requested layout once: weather for Umhlanga Rocks
at top left, sunrise and sunset underneath, and the time, weekday and date at top right. The old
bottom-left clock is moved; bottom-right video metadata remains. Automatic corner alternation is
disabled to keep the requested positions fixed. A top gradient helps readability. Subsequent
launches preserve settings changes rather than repeatedly overwriting them.

Lighthouse uses Open-Meteo's key-free personal-use endpoint. The initial coordinates are the
Umhlanga Rocks Suburb result from Open-Meteo geocoding (`-29.73941, 31.0765`). Weather is modelled
for the surrounding grid, not a physical weather station at the user's house. Temperature is
rounded for display. Sunrise/sunset use the location's timezone and today's date. Successful
requests repeat every 20 minutes; failures show "Weather unavailable" and retry after 5 minutes.
The screensaver owns and cancels the weather job, including any suspended network request.
No OpenWeather key is needed for Lighthouse. The original provider remains the default for all
upstream comparison flavors through a small `WeatherProvider` interface and application factory.
The current-weather view gains an optional second details row, empty for the original provider.

Weather location search uses Open-Meteo geocoding. Explicit coordinates remain supported; because
that service has no reverse geocoder, coordinate entries retain a GPS label instead of inventing a
city name. Existing forecast overlays use the same new provider. Weather data is CC BY 4.0 with
attribution in About and bundled assets. Free API access is for non-commercial use; review the
provider's terms before any commercial distribution. See `docs/LIGHTHOUSE-LIVE-INFO.md` for ten
researched optional additions. Only the requested font/layout/weather changes are implemented.

## Source and identity

- Fork: https://github.com/shezn05/LighthouseAerialViews
- Upstream: https://github.com/theothernt/AerialViews
- Stable base: tag `1.8.5`, commit `59a1e6667e40c33eea66725cf0f4836cb1bac9c2`.
- Custom branch: `custom/lighthouse-aerial`.
- Release application ID: `com.shezn05.lighthouseaerialviews`.
- Development application ID: `com.shezn05.lighthouseaerialviews.debug`.
- Display name: **Lighthouse Aerial Views**.
- Code namespace stays `com.neilturner.aerialviews` to limit upstream merge conflicts.
- Projectivy plugin UUID is distinct from upstream's UUID.

Use only the `lighthouse` flavor for the custom app. Original flavors remain available for
upstream comparisons and retain their original IDs. The new flavor uses the upstream F-Droid
Firebase no-op helper and does not include Firebase SDKs or process upstream Google Services
configuration. Firebase performance instrumentation is disabled at the shared plugin level;
this also affects the optional upstream comparison flavors. Local logs remain available.

No screensaver lifecycle, playback, codecs, HDR detection, refresh rate, input, playlist, media
source, storage or database behavior is intentionally changed. Shared About text now reads the
app name from resources. The upstream comparison flavors still require your own OpenWeather key
in the ignored `secrets.properties` file (`openWeatherDebug` for debug; `openWeather` for release).

## Android Studio

1. Choose **File > Open** and select this repository's root folder, then **New Window**.
2. Let Gradle sync finish. Keep the wrapper and dependency versions supplied by upstream.
3. Under **Settings > Build, Execution, Deployment > Build Tools > Gradle**, use **JDK 21**.
4. Ensure Android SDK platform **37.0** is installed in **SDK Manager**. Let the project request
   its required build tools. The installed development environment used Build Tools 36.0.0.
5. In **Build Variants**, select **lighthouseDebug** for `app`.
6. Use **Build > Assemble Project** to build without installing. Use Run only when an installation
   on the selected device is intended.

Upstream 1.8.5 requires JDK 21, Gradle 9.8.0, Android Gradle Plugin 9.4.1 and Kotlin 2.4.20.
The upstream wrapper is preserved. The min SDK remains 23 (Android 6).

## Checks

Run commands from the repository root with JDK 21 and an Android SDK configured:

```sh
./gradlew :app:testLighthouseDebugUnitTest :app:lintKotlin :app:assembleLighthouseDebug
```

On a busy Mac, limit memory and worker count without changing project files:

```sh
./gradlew :app:testLighthouseDebugUnitTest :app:lintKotlin :app:assembleLighthouseDebug \
  --max-workers=1 -Dorg.gradle.jvmargs='-Xmx1536m -XX:+UseG1GC -Dfile.encoding=UTF-8' \
  -Pkotlin.compiler.execution.strategy=in-process
```

APK: `app/build/outputs/apk/lighthouse/debug/app-lighthouse-debug.apk`.
Debug APKs use the local Android debug certificate and are development builds. Never distribute
one as a production release or assume its key is portable to another Mac.

For an untouched upstream comparison, use a separate checkout at tag `1.8.5`:

```sh
./gradlew :app:testBetaDebugUnitTest :app:lintKotlin :app:assembleBetaDebug
```

Build results, JVM tests, Android Studio sync, APK inspection, installation, and live TV testing are
separate evidence. A successful build does not prove HDR, real DreamService behavior or D-pad use.

## Release signing

Use your own persistent signing key for all future Lighthouse releases. Never use upstream's keys
or a debug key. No release key is created by this initial setup.

Copy `lighthouse-signing.properties.example` to the ignored `signing/lighthouse.properties` and
fill in your own key's details locally. `storeFile` is resolved relative to the `app` directory.
Back up the key and its passwords securely; keep both out of Git. The custom release signing
configuration is independent of the upstream release/legacy signing configurations.

Then build with `./gradlew :app:assembleLighthouseRelease`. Missing signing settings must be resolved
before producing a signed release. Verify the signed APK, its version code and certificate before
installation or distribution. Preserve that application ID and signing key for future updates.

## Screensaver and external launch components

The APK package changes; the service/activity class names stay upstream's fully qualified names.
**Do not use a shortened `.ui...` class after the new application ID.**

| Purpose | Release component |
| --- | --- |
| System screensaver | `com.shezn05.lighthouseaerialviews/com.neilturner.aerialviews.ui.screensaver.DreamActivity` |
| Preview from another app | `com.shezn05.lighthouseaerialviews/com.neilturner.aerialviews.ui.screensaver.TestActivity` |
| Settings | `com.shezn05.lighthouseaerialviews/com.neilturner.aerialviews.ui.MainActivity` |

For a development build, add `.debug` only to the package before `/`.

Changing the TV's default screensaver is a separate action from installing and previewing the app.
Before changing it, read and save the existing `screensaver_components` value so it can be restored
exactly. On TCL devices, the custom package may also need its own Auto Launch permission. Check
the current device's behavior before changing any settings. Notification access, file/media access
and other permissions must be granted to the custom app independently.

The app's data and preferences are separate from the original app. Its existing export/import format
is preserved. If both apps use the Message API simultaneously, choose different ports in settings.

## Upstream updates

`origin` points to your fork; `upstream` points to the original project. Keep upstream `master`
history intact and keep Lighthouse changes on the custom branch. Prefer a reviewed stable release
tag as the base of an update. For example, after fetching tags, merge the chosen tag into a temporary
branch based on `custom/lighthouse-aerial`, resolve conflicts there, and run all checks and TV tests
before updating the custom branch. Do not reset or force-push the custom branch to upstream.

Most custom resources and new weather logic live in `app/src/lighthouse`. Shared integration edits
cover the build flavor, baseline profile flavor, resource-based About name, weather-provider factory,
font helper and optional weather details row. Review those small shared edits when
merging upstream updates. Do not trigger the upstream store-release workflow: it expects the
original maintainer's signing infrastructure. A manual Lighthouse check workflow template is saved
at `docs/lighthouse-checks.yml.example`. To enable it later, copy it to
`.github/workflows/lighthouse.yml` using a GitHub connection with workflow write permission.
It only builds the development flavor and never publishes a release.

## Live verification before calling a release stable

Keep the running record in [the check list](docs/LIGHTHOUSE-CHECKLIST.md). The user has requested
one comprehensive check when they explicitly give the go-ahead. Accumulate pending checks as work
continues and preserve their viewing session during any necessary TV interruption.

- Coexistence with the original Aerial Views package; correct launcher name/icon/banner.
- Settings navigation, D-pad focus, Back, preview launch and exit.
- Actual system-managed DreamService start, idle timeout, wake and repeat activation.
- Sustained SDR playback, pause/skip/seek and transition between multiple videos.
- HDR/Dolby Vision and refresh rate behavior on a compatible physical TV.
- Permissions, local/network media sources, overlays and optional integrations as used.
- Signed update over an earlier Lighthouse release with preferences retained.

Record each check as passed, failed, skipped or unverified, with the device and build identity.

## Licence and attribution

The existing root `LICENSE` is preserved verbatim and also bundled in Lighthouse's assets.
Upstream credits, source history, source headers and the original README remain intact. The About
screen identifies the modified version, Neil Turner / Aerial Views and Daniel Cachapa / Aerial Dream,
and the GPLv3 licence and matching source repository.

The initial lighthouse icon and TV banner are original artwork provided under the same GPLv3
licence as this fork. `LIGHTHOUSE-NOTICE.txt` records the modification scope and date.
When distributing an APK, make the matching complete source and build instructions available under
the GPL with the licence and notices. Third-party media is not relicensed by this fork.
