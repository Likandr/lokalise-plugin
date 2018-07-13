## Pages Android gradle plugin for refresh localisation strings using lokalise.co
This is a gradle plugin for android. Refresh (upload and download) localization files using lokalise.co

## Use guide:
```///gradle project lvl:
buildscript {
    repositories {
        ..
    }
    dependencies {
	..
        classpath  'com.likandr:lokalise:1.0'
	..
    }
}
///gradle app lvl:
apply plugin: 'com.android.application'
..
apply plugin: 'lokalise'
..
android {
    ..
}
dependencies {
    ..
}
//it important and should be specified id(project) and token(api with r/w the access rights)
lokalise {
    id "346194865b3caa7e85d8b1.25144699"
    token "55f109a3843351116255b79dc046b7a02fd38fdb"
}

//if need autoupdate strings before build uncomment the following line[1]: 
//preBuild.dependsOn "refreshStrings"
```

P.s. List of available manipulations is in the gradle folder lokalise
//P.p.s. The plugin itself recognizes the default language of the project from lokalise

[1]	replace=0(when unloading) --- replace existing translations of the keys imported - disabled. But when downloading transfers will be overwritten.
