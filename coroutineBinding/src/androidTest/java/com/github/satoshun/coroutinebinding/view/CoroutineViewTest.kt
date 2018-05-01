package com.github.satoshun.coroutinebinding.view

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import com.github.satoshun.coroutinebinding.ViewActivity
import com.google.common.truth.Truth
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoroutineViewTest {

  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  @Test @UiThreadTest
  fun attaches() = runBlocking<Unit> {
    val child = TextView(rule.activity)
    val attaches = child.attaches(1)
    rule.activity.view.addView(child)
    Truth.assertThat(attaches.poll()).isNotNull()
    rule.activity.view.removeView(child)

    attaches.cancel()
    rule.activity.view.addView(child)
    Truth.assertThat(attaches.poll()).isNull()
  }

  @Test @UiThreadTest
  fun detaches() = runBlocking<Unit> {
    val child = TextView(rule.activity)
    val detaches = child.detaches(1)
    rule.activity.view.addView(child)
    rule.activity.view.removeView(child)
    Truth.assertThat(detaches.poll()).isNotNull()

    detaches.cancel()
    rule.activity.view.addView(child)
    rule.activity.view.removeView(child)
    Truth.assertThat(detaches.poll()).isNull()
  }

  @Test @UiThreadTest
  fun clicks() = runBlocking<Unit> {
    val clicks = rule.activity.view.clicks(1)

    rule.activity.view.performClick()
    Truth.assertThat(clicks.poll()).isNotNull()

    clicks.cancel()
    rule.activity.view.performClick()
    Truth.assertThat(clicks.poll()).isNull()
  }

  @Test @UiThreadTest
  fun focusChanges() = runBlocking<Unit> {
    val focus = rule.activity.view.focusChanges(1)
    rule.activity.view.isFocusable = true
    rule.activity.view.requestFocus()
    Truth.assertThat(focus.poll()).isTrue()
    rule.activity.view.clearFocus()
    Truth.assertThat(focus.poll()).isFalse()

    focus.cancel()
    rule.activity.view.requestFocus()
    Truth.assertThat(focus.poll()).isNull()
  }

  @Test @UiThreadTest
  fun globalLayouts() = runBlocking<Unit> {
    val layouts = rule.activity.view.globalLayouts(1)
    rule.activity.view.viewTreeObserver.dispatchOnGlobalLayout()
    Truth.assertThat(layouts.poll()).isNotNull()

    layouts.cancel()
    rule.activity.view.viewTreeObserver.dispatchOnGlobalLayout()
    Truth.assertThat(layouts.poll()).isNull()
  }
}
