[![Build Status](https://travis-ci.org/jsabino/chuck-norris.svg?branch=master)](https://travis-ci.org/jsabino/chuck-norris)

# chuck-norris

A sample android application that consume data of [Chuck Norris Jokes Api](https://api.chucknorris.io/)

## Building and Running

### Running from IDE

- Use Android Studio 3.3.2 or newer

### Building the APK

Run the task

```
./gradlew assembleDebug
```

The apk will be generated in ./app/build/outputs/apk/release

### Running unit tests

```
./gradlew test
```

### Running acceptance tests

Connect a device (usb or emulator) and run

```
./gradlew connectedCheck
```

## Used Technologies

- Kotlin
- MVVM pattern
- Kodein for dependency injection
- Room for persistence
- RxJava2 for reactive programming
- Retrofit for http requests
- LiveData as a lifecycle aware data holder
- Mockito for mocking dependencies in tests
- Espresso for acceptance tests (ui)
