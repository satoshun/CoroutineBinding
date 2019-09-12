object Vers {
  const val compile_sdk = 28
  const val min_sdk = 15
  const val target_sdk = 28
  const val agp = "3.5.0"

  const val kotlin = "1.3.50"
  const val couroutine = "1.2.2"
}

object Libs {
  const val android_plugin = "com.android.tools.build:gradle:${Vers.agp}"
  const val kotlin_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Vers.kotlin}"
  const val ktlint_plugin = "gradle.plugin.org.jlleitschuh.gradle:ktlint-gradle:4.1.0"
  const val dokka_plugin = "org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.17"
  const val publish_plugin = "com.vanniktech:gradle-maven-publish-plugin:0.6.0"

  const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Vers.kotlin}"
  const val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Vers.couroutine}"
  const val ui_coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Vers.couroutine}"

  const val material = "com.google.android.material:material:1.0.0"
  const val appcompat = "androidx.appcompat:appcompat:1.0.2"
  const val annotations = "androidx.annotation:annotation:1.0.0"
  const val recyclerview = "androidx.recyclerview:recyclerview:1.0.0"
  const val constraint_layout = "androidx.constraintlayout:constraintlayout:2.0.0-beta2"

  const val core = "androidx.core:core:1.0.0"
  const val viewpager = "androidx.viewpager:viewpager:1.0.0"
  const val drawerlayout = "androidx.drawerlayout:drawerlayout:1.0.0"
  const val slidingpanelayout = "androidx.slidingpanelayout:slidingpanelayout:1.0.0"
  const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0"

  const val junit = "junit:junit:4.12"
  const val test_runner = "androidx.test:runner:1.1.1"
  const val test_rule = "androidx.test:rules:1.1.1"
  const val test_junit_runner = "androidx.test.ext:junit:1.0.0"
  const val espresso = "androidx.test.espresso:espresso-core:3.2.0"
  const val espresso_contrib = "androidx.test.espresso:espresso-contrib:3.2.0"

  const val truth = "com.google.truth:truth:1.0"
  const val multidex = "androidx.multidex:multidex:2.0.1"
}
