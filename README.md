# Vitabu 📚

Android application for reading storybooks and expanding word vocabulary.

Corresponding EGRA skill: `FAMILIAR_WORD_READING`

![device-2020-12-14-173528](https://user-images.githubusercontent.com/15718174/102108186-4796f480-3e3b-11eb-9375-4dcc53b60d7e.png)

<img width="320" alt="device-2020-06-10-152302" src="https://user-images.githubusercontent.com/15718174/84238987-6e900900-ab2e-11ea-82d5-c41a510473dd.png">

## Installation

Note: This app depends on the [elimu.ai Content Provider](https://github.com/elimu-ai/content-provider) to be installed.

## Development 👩🏽‍💻

Compile APK:

```
./gradlew clean build
```

Install APK:

```
adb install app/build/outputs/apk/debug/ai.elimu.vitabu-<versionCode>-debug.apk
```

Perform a release:

```
./gradlew releaseClean
./gradlew releasePrepare -PbumpType=patch
./gradlew releasePerform
```

---

## About elimu.ai

![elimu ai-tagline](https://user-images.githubusercontent.com/15718174/54360503-e8e88980-465c-11e9-9792-32b513105cf3.png)

 * For a high-level description of the project, see https://github.com/elimu-ai/wiki#readme.
 * For project milestones, see https://github.com/elimu-ai/wiki/projects.
