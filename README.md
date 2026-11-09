# EVEE Tune

EVEE Tune is an independent Android app rebrand based on [LastWave Native](https://github.com/Clash-Projects/LastWave-Native).

## Current status
- Repository scaffold and source-import workflow are committed.
- The Android source is imported by the GitHub Actions workflow; it is **not yet present** until that workflow completes.
- App label target: **EVEE Tune**
- Android application ID target: `com.evee.tune`
- Upstream Kotlin namespace remains `com.lastwave.app` to preserve internal component references.

## Import and build
1. Open the **Actions** tab.
2. Select **Import EVEE Tune Android source**.
3. Tap **Run workflow** on the `main` branch.
4. Wait for the workflow to import the upstream source and attempt a debug build.
5. If the build succeeds, open the completed run and download the **EVEE-Tune-debug** artifact.

The workflow imports the upstream source, preserves the upstream license and notices, applies the EVEE Tune launcher label and application ID, commits the imported source to `main`, and attempts `./gradlew :app:assembleDebug`.

## Local build
Use Android Studio with JDK 17 and Android SDK 37:

```bash
./gradlew :app:assembleDebug
```

## Attribution and license
This project is based on LastWave Native by its upstream contributors. Upstream copyright notices and the GPL-3.0 license are preserved. See [LICENSE](LICENSE) and the [upstream repository](https://github.com/Clash-Projects/LastWave-Native).

EVEE Tune is independent and is not affiliated with or endorsed by the upstream developers or any music service. All third-party names and trademarks belong to their respective owners.
