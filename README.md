# StyleableShareDialog

[![](https://jitpack.io/v/ogiba/StyleableShareDialog.svg)](https://jitpack.io/#ogiba/StyleableShareDialog)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/ogiba/StyleableShareDialog/blob/master/LICENSE)


Open source library that allows to quickly create ShareDialog with some customization.

Library is still in development, current stable version is 0.1.0 .

---

# Gradle Dependency

### Repository

The Gradle dependency is available via [JitPack](https://jitpack.io/#ogiba/StyleableShareDialog).

To use JitPack repository you need to add to your root build.gradle line that it at the end of repositories:

```gradle
allprojects {
    repositories {
	... 
        maven { url 'https://jitpack.io' }
    }
}
```

And as next step add dependency to project:

```gradle
dependencies {
    compile 'com.github.ogiba:StyleableShareDialog:v0.1.0'
}
```

The minimum API level supported by this library is API 16 (Jelly Bean)

---

# What's new
Check project's Releases page to find what was changed according to previous version of library

### [Check Releases](https://github.com/ogiba/StyleableShareDialog/releases)

---

# Basic usage

Creating simple share dialog provided by this library, which allows you to share string values is very easy. You just need to create new instance of ShareDialog, give him a value and show it using FragmentManager or FragmentTransaction.

```java
new ShareDialog.newInstance().
        .setShareContent("Some string value")
        .show(getFragmentManager());
```

---

# Creating ShareDialog via Builder

You can easily set more complex properties for ShareDialog instance. To achieve it you need to use Builder mechanism implemented in library. Builder allows you to set diffrenet kind of properites for dialog (e.g. `title`, `titleColorRes`, `customHeaderRes` and `dialogRatioSize`)

```java
new ShareDialog.Builder()
        .setTitle("Custom dialog title")
        .setTitleTintColor(Color.Red)
        .setHeaderLayout(R.layout.customHeader)
        .setSizeRatio(new Ratio(1.0,0.5))
        .build()
        .setShareContent("Some string value")
        .show(getFragmentManager())
```

---

As wrote on the top of document library is still in development. Enjoy current version and please be patient.