# Waresys for Android

The Android mobile app is used to manage the warehouse system, NFC is used to quickly retrieve warehouse item information.

## Build

Requirements:
 * [JDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html) 8+
 * [Android SDK](https://developer.android.com/studio/#command-tools) v2.7+


Clone the `waresys` repository using git:

```bash
$ git clone https://github.com/kohlicekjan/waresys.git
$ cd waresys/src/waresys-android
```

Start the build:

```bash
$ gradlew assembleDebug
```

Output directory is `./app/build/outputs/apk/debug`
