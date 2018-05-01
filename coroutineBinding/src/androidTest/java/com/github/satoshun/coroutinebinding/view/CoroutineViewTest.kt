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
  }

  @Test @UiThreadTest
  fun attaches__with_cancel() = runBlocking<Unit> {
    val child = TextView(rule.activity)
    val attaches = child.attaches(1)
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
  }

  @Test @UiThreadTest
  fun detaches__with_cancel() = runBlocking<Unit> {
    val child = TextView(rule.activity)
    val detaches = child.detaches(1)
    rule.activity.view.addView(child)
    detaches.cancel()
    rule.activity.view.removeView(child)
    Truth.assertThat(detaches.poll()).isNull()
  }

  @Test @UiThreadTest
  fun clicks() = runBlocking<Unit> {
    val clicks = rule.activity.view.clicks(1)

    rule.activity.view.performClick()
    Truth.assertThat(clicks.poll()).isNotNull()
  }

  @Test @UiThreadTest
  fun clicks__with_cancel() {
    val clicks = rule.activity.view.clicks(1)
    clicks.cancel()
    rule.activity.view.performClick()
    Truth.assertThat(clicks.poll()).isNull()
  }
}
