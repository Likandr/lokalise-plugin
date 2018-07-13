## Android gradle plugin for refresh localisation strings using lokalise.co
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
}

//if need autoupdate strings before build uncomment the following line[1]: 
//preBuild.dependsOn "refreshStrings"
```

P.s. List of available manipulations is in the gradle folder lokalise.

P.p.s. The plugin itself recognizes the default language of the project from lokalise.

> [1]	run with param `replace=0`(when uploading) --- replace existing translations of the keys imported - disabled. But when downloading transfers will be overwritten.
