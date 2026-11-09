# EVEE Tune

EVEE Tune is an independent Android app rebrand based on [LastWave Native](https://github.com/Clash-Projects/LastWave-Native).

## Current build identity
- App label: **EVEE Tune**
- Android application ID: `com.evee.tune`
- Upstream code namespace: `com.lastwave.app` (kept internally to avoid breaking upstream component references)
- Upstream baseline: verified build commit `b3234549a51b1d77ab9c50fabfe88ea0b869380f` (LastWave v5.1.2)
- Build: GitHub Actions debug APK

## Build
Open **Actions → Import EVEE Tune Android source → Run workflow**. The workflow imports the upstream Android source, applies the EVEE Tune app label and application ID, commits the source, and attempts a debug APK build. The APK is uploaded as an Actions artifact when the build succeeds.

For a local build, use Android Studio with JDK 17 and Android SDK 37, then run:

```bash
./gradlew :app:assembleDebug
```

## Attribution and license
This project is based on LastWave Native by its upstream contributors. Upstream copyright notices and the GPL-3.0 license are preserved in this repository. See [LICENSE](LICENSE) and the [upstream repository](https://github.com/Clash-Projects/LastWave-Native).

EVEE Tune is independent and is not affiliated with or endorsed by the upstream developers or any music service. All third-party names and trademarks belong to their respective owners.
