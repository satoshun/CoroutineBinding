package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import com.github.satoshun.coroutinebinding.support.v7.ViewActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineActionMenuViewTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  @Before @UiThreadTest
  fun setUp() {
  }

  @Test
  fun itemClicks() {
  }
}
