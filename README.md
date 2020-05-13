# [HelpfulTools](https://github.com/jakepurple13/HelpfulTools/wiki)

![GitHub license](https://img.shields.io/github/license/jakepurple13/HelpfulTools?style=for-the-badge)
![GitHub top language](https://img.shields.io/github/languages/top/jakepurple13/helpfultools?style=for-the-badge)
![JitPack](https://img.shields.io/jitpack/v/github/jakepurple13/HelpfulTools?style=for-the-badge)

[![Dependabot Status](https://flat.badgen.net/dependabot/jakepurple13/HelpfulTools?icon=dependabot)](https://dependabot.com)

Just some small simple tools that I have found myself copying+pasting to almost all projects I do.

First, add JitPack:
```groovy
	allprojects {
		repositories {
			//...
			maven { url 'https://jitpack.io' }
		}
	}
```

[![](https://jitpack.io/v/jakepurple13/HelpfulTools.svg?style=flat-square)](https://jitpack.io/#jakepurple13/HelpfulTools)

```groovy
implementation 'com.github.jakepurple13.HelpfulTools:flowutils:{version}'
implementation 'com.github.jakepurple13.HelpfulTools:gsonutils:{version}'
implementation 'com.github.jakepurple13.HelpfulTools:helpfulutils:{version}'
implementation 'com.github.jakepurple13.HelpfulTools:loggingutils:{version}'
implementation 'com.github.jakepurple13.HelpfulTools:rxutils:{version}'
implementation 'com.github.jakepurple13.HelpfulTools:dragswipe:{version}'
implementation 'com.github.jakepurple13.HelpfulTools:funutils:{version}'
```

For the dslprocessor module, also include:
```groovy
//To allow kapt
apply plugin: 'kotlin-kapt'
//For the Annotations
implementation 'com.github.jakepurple13.HelpfulTools:dslannotations:{version}'
//For the actual generation
kapt "com.github.jakepurple13.HelpfulTools:dslprocessor:{version}"
```
