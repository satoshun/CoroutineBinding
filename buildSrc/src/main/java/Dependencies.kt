object Vers {
  val compile_sdk = 27
  val min_sdk = 15
  val target_sdk = 27
  val agp = "3.1.1"

  val kotlin = "1.2.40"
  val couroutine = "0.22.5"
  val support_lib = "27.1.1"
  val ktlint = "0.21.0"
}

object Libs {
  val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jre7:${Vers.kotlin}"
  val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Vers.couroutine}"

  val viewmodel = "android.arch.lifecycle:viewmodel:1.1.1"
  val livedata = "android.arch.lifecycle:livedata:1.1.1"
  val gms = "com.google.android.gms:play-services-location:15.0.0"

  val junit = "junit:junit:4.12"
  val support_test = "com.android.support.test:runner:1.0.1"
  val espresso = "com.android.support.test.espresso:espresso-core:3.0.1"
  val arch_test = "android.arch.core:core-testing:1.1.1"

  val appcompat = "com.android.support:appcompat-v7:${Vers.support_lib}"
  val support_v4 = "com.android.support:support-v4:${Vers.support_lib}"

  val truth = "com.google.truth:truth:0.39"
}
