@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding

import com.google.common.truth.Truth

inline fun Any?.isNull() {
  Truth.assertThat(this).isNull()
}

inline fun Any?.isNotNull() {
  Truth.assertThat(this).isNotNull()
}

inline fun Any?.isEqualTo(other: Any?) {
  Truth.assertThat(this).isEqualTo(other)
}

fun <T> T.verify() = com.nhaarman.mockito_kotlin.verify(this)
