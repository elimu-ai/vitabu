# Vitabu 📚

Android application for reading storybooks and expanding word vocabulary.

Corresponding [EGRA skill](https://github.com/elimu-ai/model/blob/main/src/main/java/ai/elimu/model/v2/enums/content/LiteracySkill.java): `FAMILIAR_WORD_READING`
> Children’s reading skills are often assessed using reading lists of unrelated words. This allows for a purer measure of word recognition and decoding skills than does reading connected text, as children are unable to guess the next word from the context when reading lists of unrelated words. For this assessment, familiar words are high-frequency words selected from first-, second-, and third-grade reading materials and storybooks in the language and context.
    
> <img width="320" alt="FAMILIAR_WORD_READING" src="https://raw.githubusercontent.com/elimu-ai/webapp/main/src/main/webapp/static/img/admin/EGRA_FAMILIAR_WORD_READING.png" />

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

elimu.ai - Free personalized learning for every child on Earth 🌍🌏🌎

[Website 🌐](https://elimu.ai) &nbsp; [Wiki 📃](https://github.com/elimu-ai/wiki#readme) &nbsp; [Projects 👩🏽‍💻](https://github.com/elimu-ai/wiki/projects) &nbsp; [Milestones 🎯](https://github.com/elimu-ai/wiki/milestones) &nbsp; [Community 👋🏽](https://github.com/elimu-ai/wiki#open-source-community)
