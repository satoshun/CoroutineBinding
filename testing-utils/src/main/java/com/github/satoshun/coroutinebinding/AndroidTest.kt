package com.github.satoshun.coroutinebinding

import android.app.Activity
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class AndroidTest<T : Activity>(private val clazz: Class<T>) {
  @JvmField @Rule val rule = ActivityTestRule<T>(clazz)
}
