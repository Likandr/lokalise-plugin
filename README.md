## Android gradle plugin for refresh localisation strings using lokalise.co

[![CircleCI](https://circleci.com/gh/Likandr/lokalise-plugin.svg?style=svg)](https://circleci.com/gh/Likandr/lokalise-plugin) 

This is a gradle plugin for android. Refresh (upload and download) localization files using lokalise.co

## Use guide:
### in gradle, project level:
```groovy
buildscript {
    repositories {
        ..
    }
    dependencies {
	..
        classpath  'com.github.likandr:lokalise-plugin:1.3'
	..
    }
}
```
### in gradle, app level:
```groovy
apply plugin: 'com.android.application'
..
apply plugin: 'com.likandr.lokalise'
..
android {
    ..
}
dependencies {
    ..
}
//it important and should be specified id(project) and token(api with r/w the access rights)
lokalise {
    id "XXXXXXXXXXXXXXXXXXXXXX.XXXXXXXX"
    token "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

    // optional: you can also specify additional options for use during downloading the translations
    downloadConfig {
        // all values here have their default value as present in this snippet

        // Enable to use original filenames/formats. If set to false all keys will be export to a single file per language.
        originalFilenames = false

        // Export key sort mode
        order = "first_added" //one of: first_added, last_added, last_updated, a_z, z_a

        // Select how you would like empty translations to be exported. Allowed values are empty to keep empty, base to replace with the base language value, or skip to omit
        emptyTranslationStrategy = "skip" //one of: empty, base, skip
    }
}

//if need autoupdate strings before build uncomment the following line[1]: 
//preBuild.dependsOn "refreshStrings"
```

P.s. List of available manipulations is in the gradle folder lokalise.

P.p.s. The plugin itself recognizes the default language of the project from lokalise.

> [1]	run with param `replace=0`(when uploading) --- replace existing translations of the keys imported - disabled. But when downloading transfers will be overwritten.
