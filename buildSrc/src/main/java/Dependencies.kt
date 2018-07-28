object Vers {
  val compile_sdk = 28
  val min_sdk = 15
  val target_sdk = 28
  val agp = "3.2.0-beta04"

  val kotlin = "1.2.51"
  val couroutine = "0.24.0"
  val support_lib = "27.1.1"
}

object Libs {
  val android_plugin = "com.android.tools.build:gradle:${Vers.agp}"
  val kotlin_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Vers.kotlin}"
  val ktlint_plugin = "gradle.plugin.org.jlleitschuh.gradle:ktlint-gradle:4.1.0"
  val dokka_plugin = "org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.17"

  val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Vers.kotlin}"
  val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Vers.couroutine}"
  val ui_coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Vers.couroutine}"

  val design = "com.android.support:design:${Vers.support_lib}"
  val support_v4_core = "com.android.support:support-core-ui:${Vers.support_lib}"
  val appcompat_v7 = "com.android.support:appcompat-v7:${Vers.support_lib}"
  val support_annotations = "com.android.support:support-annotations:${Vers.support_lib}"
  val recyclerview = "com.android.support:recyclerview-v7:${Vers.support_lib}"
  val constraint_layout = "com.android.support.constraint:constraint-layout:1.1.1"

  val constraint_layout2 = "androidx.constraintlayout:constraintlayout:2.0.0-alpha1"

  val viewmodel = "android.arch.lifecycle:viewmodel:1.1.1"
  val livedata = "android.arch.lifecycle:livedata:1.1.1"
  val gms = "com.google.android.gms:play-services-location:15.0.0"

  val junit = "junit:junit:4.12"
  val support_test = "com.android.support.test:runner:1.0.1"
  val espresso = "com.android.support.test.espresso:espresso-core:3.0.1"
  val arch_test = "android.arch.core:core-testing:1.1.1"

  val appcompat = "com.android.support:appcompat-v7:${Vers.support_lib}"
  val support_v4 = "com.android.support:support-v4:${Vers.support_lib}"

  val truth = "com.google.truth:truth:0.40"
  val mockito_kotlin = "com.nhaarman:mockito-kotlin-kt1.1:1.5.0"
  val multidex = "com.android.support:multidex:1.0.3"
}
