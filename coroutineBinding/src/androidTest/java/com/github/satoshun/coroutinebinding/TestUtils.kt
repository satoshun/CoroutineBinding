@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding

import com.google.common.truth.Truth
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.runBlocking

inline fun Any?.isNull() {
  Truth.assertThat(this).isNull()
}

inline fun Any?.isNotNull() {
  Truth.assertThat(this).isNotNull()
}

inline fun Any?.isEqualTo(other: Any?) {
  Truth.assertThat(this).isEqualTo(other)
}

inline fun <T> T.verify() = com.nhaarman.mockito_kotlin.verify(this)

inline fun <T> uiRunBlocking(noinline block: suspend CoroutineScope.() -> T): T {
  return runBlocking(UI, block)
}
