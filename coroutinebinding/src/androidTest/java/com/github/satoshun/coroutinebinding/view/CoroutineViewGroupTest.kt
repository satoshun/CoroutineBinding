package com.github.satoshun.coroutinebinding.view

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isInstanceOf
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoroutineViewGroupTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private val view: ViewGroup get() = rule.activity.view

  @Test
  fun changeEvents() = testRunBlocking {
    val view = view
    val child = uiRunBlocking { View(rule.activity) }
    val changeEvents = view.changeEvents(1)

    uiLaunch { view.addView(child) }
    changeEvents.receive().isInstanceOf(ViewGroupHierarchyChildViewAddEvent::class)

    uiLaunch { view.removeView(child) }
    changeEvents.receive().isInstanceOf(ViewGroupHierarchyChildViewRemoveEvent::class)

    changeEvents.cancel()
    uiLaunch { view.addView(child) }
    changeEvents.poll().isNull()
  }
}
