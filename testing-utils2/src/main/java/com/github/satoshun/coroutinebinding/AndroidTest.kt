package com.github.satoshun.coroutinebinding

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class AndroidTest<T : Activity>(private val clazz: Class<T>) {
  @JvmField @Rule val rule = ActivityTestRule<T>(clazz)
}
