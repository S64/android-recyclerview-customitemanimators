## android-recyclerview-customitemanimaors

Custom ItemAnimators for RecyclerView.

![](./assets/screenrecord1.gif)


## Demo

| CustomizableDefaultItemAnimator | FadeLiftUpItemAnimator |
|---------------------------------|------------------------|
| <img src="./assets/01_customizable_default.gif" width="300"/> | <img src="./assets/02_fade-liftup.gif" width="300"/> |

| LiftUpItemAnimator | PlaceAndFadeLiftUpItemAnimator |
|--------------------|--------------------------------|
| <img src="./assets/03_liftup.gif" width="300"/> | <img src="./assets/04_place_fade_liftup.gif" width="300"/> |

## Installation

```groovy
buildscript {
    ext {
        customitemanimators_version = '0.0.2'
    }
}
```

```groovy
repositories {
    maven { url 'http://dl.bintray.com/s64/maven' }
}

dependencies {
    implementation "jp.s64.android.recyclerview.customitemanimators:core:${customitemanimators_version}" // .core.CustomizableDefaultItemAnimator
    // implementation "jp.s64.android.recyclerview.customitemanimators:liftup:${customitemanimators_version}" // .liftup.*
}
```

## Apps using android-recyclerview-customitemanimators

- [Balloon](https://play.google.com/store/apps/details?id=jp.co.fowd.balloon)

## License

```
Copyright 2018 Shuma Yoshioka

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
