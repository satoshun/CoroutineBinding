package com.github.satoshun.coroutinebinding

import com.nhaarman.mockito_kotlin.verify

fun <T> T.verify() = verify(this)
