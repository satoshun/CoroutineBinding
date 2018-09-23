object Vers {
  val compile_sdk = 28
  val min_sdk = 15
  val target_sdk = 28
  val agp = "3.2.0-rc03"

  val kotlin = "1.2.70"
  val couroutine = "0.26.1"
}

object Libs {
  val android_plugin = "com.android.tools.build:gradle:${Vers.agp}"
  val kotlin_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Vers.kotlin}"
  val ktlint_plugin = "gradle.plugin.org.jlleitschuh.gradle:ktlint-gradle:4.1.0"
  val dokka_plugin = "org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.17"

  val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Vers.kotlin}"
  val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Vers.couroutine}"
  val ui_coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Vers.couroutine}"

  val material = "com.google.android.material:material:1.0.0"
  val appcompat = "androidx.appcompat:appcompat:1.0.0"
  val annotations = "androidx.annotation:annotation:1.0.0"
  val recyclerview = "androidx.recyclerview:recyclerview:1.0.0"
  val constraint_layout = "androidx.constraintlayout:constraintlayout:2.0.0-alpha1"

  val core = "androidx.core:core:1.0.0"
  val viewpager = "androidx.viewpager:viewpager:1.0.0"
  val drawerlayout = "androidx.drawerlayout:drawerlayout:1.0.0"
  val slidingpanelayout = "androidx.slidingpanelayout:slidingpanelayout:1.0.0"
  val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0"

  val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.0.0"
  val livedata = "androidx.lifecycle:lifecycle-livedata:2.0.0"
  val lifecycle_compiler = "androidx.lifecycle:lifecycle-compiler:2.0.0"

  val junit = "junit:junit:4.12"
  val test_runner = "androidx.test:runner:1.1.0-alpha4"
  val test_rule = "androidx.test:rules:1.1.0-alpha4"
  val espresso = "androidx.test.espresso:espresso-core:3.1.0-alpha4"
  val espresso_contrib = "androidx.test.espresso:espresso-contrib:3.1.0-alpha4"
  val arch_test = "android.arch.core:core-testing:1.1.1"

  val truth = "com.google.truth:truth:0.42"
  val mockito_kotlin = "com.nhaarman:mockito-kotlin-kt1.1:1.5.0"
  val multidex = "androidx.multidex:multidex:2.0.0"
}
