[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.satoshun.coroutinebinding/coroutinebinding/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.satoshun.coroutinebinding/coroutinebinding)
[![CircleCI](https://circleci.com/gh/satoshun/CoroutineBinding/tree/master.svg?style=svg)](https://circleci.com/gh/satoshun/CoroutineBinding/tree/master)

# CoroutineBinding

Coroutine binding APIs for Android UI widgets from the platform and support libraries.


## Download

Platform bindings:
```groovy
implementation 'com.github.satoshun.coroutinebinding:coroutinebinding:{latest-version}'
```

'support-v4' library bindings:
```groovy
implementation 'com.github.satoshun.coroutinebinding:coroutinebinding-support-v4:{latest-version}'
```

'appcompat-v7' library bindings:
```groovy
implementation 'com.github.satoshun.coroutinebinding:coroutinebinding-appcompat-v7:{latest-version}'
```

'design' library bindings:
```groovy
implementation 'com.github.satoshun.coroutinebinding:coroutinebinding-design:{latest-version}'
```

'recyclerview-v7' library bindings:
```groovy
implementation 'com.github.satoshun.coroutinebinding:coroutinebinding-recyclerview-v7:{latest-version}'
```

## what is this?

Android event (e.g., view clicks) is transformed to [ReceiveChannel](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental.channels/-receive-channel/receive.html).

This project is inspired by [RxBinding](https://github.com/JakeWharton/RxBinding). This library APIs is same RxBinding!


## etc

This library borrows so many many many codes from RxBinding. Thx!


## License

```
Copyright (C) 2018 Sato Shun

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
