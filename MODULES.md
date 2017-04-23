Modules
===============

Library is also distributed via **separate modules** which may be downloaded as standalone parts of
the library in order to decrease dependencies count in Android projects, so only dependencies really
needed in an Android project are included. **However** some modules may depend on another modules
from this library or on modules from other libraries.

Below are listed modules that are available for download also with theirs dependencies.

## Download ##

### Gradle ###

For **successful resolving** of artifacts for separate modules via **Gradle** add the following snippet
into **build.gradle** script of your desired Android project and use `compile '...'` declaration
as usually.

    repositories {
        maven {
            url  "http://dl.bintray.com/universum-studios/android"
        }
    }

## Available modules ##
> Following modules are available in the [latest](https://github.com/universum-studios/android_settings/releases "Latest Releases page") release.

**!!! Note, that the separate modules will be available for download after 1.0.0 version is released !!!**

- **[Core](https://github.com/universum-studios/android_settings/tree/master/library-core)**
- **[Activity](https://github.com/universum-studios/android_settings/tree/master/library-activity)**
- **[Fragment](https://github.com/universum-studios/android_settings/tree/master/library-fragment)**
- **[Menu](https://github.com/universum-studios/android_settings/tree/master/library-menu)**
- **[Selection](https://github.com/universum-studios/android_settings/tree/master/library-selection)**
- **[Slider](https://github.com/universum-studios/android_settings/tree/master/library-slider)**
- **[Empty](https://github.com/universum-studios/android_settings/tree/master/library-empty)**
- **[@Dialog](https://github.com/universum-studios/android_settings/tree/master/library-dialog_group)**
- **[Dialog-Base](https://github.com/universum-studios/android_settings/tree/master/library-dialog-base)**
- **[Dialog-Collection](https://github.com/universum-studios/android_settings/tree/master/library-dialog-collection)**
- **[Dialog-Edit](https://github.com/universum-studios/android_settings/tree/master/library-dialog-edit)**
- **[Dialog-DateTime](https://github.com/universum-studios/android_settings/tree/master/library-dialog-datetime)**
- **[Dialog-Color](https://github.com/universum-studios/android_settings/tree/master/library-dialog-color)**
