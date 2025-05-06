[![](https://jitpack.io/v/ai.elimu/vitabu.svg)](https://jitpack.io/#ai.elimu/vitabu)

# Vitabu 📚

Android application for reading storybooks and expanding word vocabulary.

Literacy skills:
* [`FAMILIAR_WORD_READING`](https://github.com/elimu-ai/wiki/blob/main/literacy-skills/FAMILIAR_WORD_READING.md)

![device-2020-12-14-173528](https://user-images.githubusercontent.com/15718174/102108186-4796f480-3e3b-11eb-9375-4dcc53b60d7e.png)

<img width="320" alt="device-2020-06-10-152302" src="https://user-images.githubusercontent.com/15718174/84238987-6e900900-ab2e-11ea-82d5-c41a510473dd.png">

## Installation

> [!IMPORTANT]
> Note: This app depends on the [elimu.ai Content Provider](https://github.com/elimu-ai/content-provider) to be installed.

## Development 👩🏽‍💻

Compile APK:

```
./gradlew clean build
```

Install APK:

```
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Content Provider Utils 📦

If you want to make changes to the elimu.ai Content Provider's utility library, see testing instructions at https://github.com/elimu-ai/content-provider/blob/main/README.md#utils-snapshot

### Release 📦

To perform a release, follow these steps:

1. Merge your PR into the `main` branch
1. Wait for the ["Gradle Release"](https://github.com/elimu-ai/vitabu/actions/workflows/gradle-release.yml) workflow to complete
1. Ensure that the new release version appears at https://github.com/elimu-ai/vitabu/releases

---

<p align="center">
  <img src="https://github.com/elimu-ai/webapp/blob/main/src/main/webapp/static/img/logo-text-256x78.png" />
</p>
<p align="center">
  elimu.ai - Free open-source learning software for out-of-school children 🚀✨
</p>
<p align="center">
  <a href="https://elimu.ai">Website 🌐</a>
  &nbsp;•&nbsp;
  <a href="https://github.com/elimu-ai/wiki#readme">Wiki 📃</a>
  &nbsp;•&nbsp;
  <a href="https://github.com/orgs/elimu-ai/projects?query=is%3Aopen">Projects 👩🏽‍💻</a>
  &nbsp;•&nbsp;
  <a href="https://github.com/elimu-ai/wiki/milestones">Milestones 🎯</a>
  &nbsp;•&nbsp;
  <a href="https://github.com/elimu-ai/wiki#open-source-community">Community 👋🏽</a>
  &nbsp;•&nbsp;
  <a href="https://www.drips.network/app/drip-lists/41305178594442616889778610143373288091511468151140966646158126636698">Support 💜</a>
</p>
