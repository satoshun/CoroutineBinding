package com.github.satoshun.coroutinebinding.view

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.satoshun.coroutinebinding.ViewActivity
import com.google.common.truth.Truth
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoroutineViewTest {

  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private val view: ViewGroup get() = rule.activity.view

  @Test @UiThreadTest
  fun attaches() = runBlocking<Unit> {
    val child = TextView(rule.activity)
    val attaches = child.attaches(1)
    view.addView(child)
    Truth.assertThat(attaches.poll()).isNotNull()
    view.removeView(child)

    attaches.cancel()
    view.addView(child)
    Truth.assertThat(attaches.poll()).isNull()
  }

  @Test @UiThreadTest
  fun detaches() = runBlocking<Unit> {
    val child = TextView(rule.activity)
    val detaches = child.detaches(1)
    view.addView(child)
    view.removeView(child)
    Truth.assertThat(detaches.poll()).isNotNull()

    detaches.cancel()
    view.addView(child)
    view.removeView(child)
    Truth.assertThat(detaches.poll()).isNull()
  }

  @Test @UiThreadTest
  fun clicks() = runBlocking<Unit> {
    val clicks = view.clicks(1)

    view.performClick()
    Truth.assertThat(clicks.poll()).isNotNull()

    clicks.cancel()
    view.performClick()
    Truth.assertThat(clicks.poll()).isNull()
  }

  @Ignore("todo")
  @Test @UiThreadTest
  fun drags() = runBlocking<Unit> {
    val drags = view.drags(1)
  }

  @Ignore("todo")
  @Test @UiThreadTest
  fun draws() = runBlocking<Unit> {
    val draws = view.draws(1)
  }

  @Test @UiThreadTest
  fun focusChanges() = runBlocking<Unit> {
    val focus = view.focusChanges(1)
    view.isFocusable = true
    view.requestFocus()
    Truth.assertThat(focus.poll()).isTrue()
    view.clearFocus()
    Truth.assertThat(focus.poll()).isFalse()

    focus.cancel()
    view.requestFocus()
    Truth.assertThat(focus.poll()).isNull()
  }

  @Test @UiThreadTest
  fun globalLayouts() = runBlocking<Unit> {
    val layouts = view.globalLayouts(1)
    view.viewTreeObserver.dispatchOnGlobalLayout()
    Truth.assertThat(layouts.poll()).isNotNull()

    layouts.cancel()
    view.viewTreeObserver.dispatchOnGlobalLayout()
    Truth.assertThat(layouts.poll()).isNull()
  }

  @Ignore("todo")
  @Test @UiThreadTest
  fun hovers() = runBlocking<Unit> {
    val hovers = view.hovers(1)
  }

  @Test @UiThreadTest
  fun layoutChanges() = runBlocking<Unit> {
    val layoutChange = view.layoutChanges(1)
    view.layout(0, 0, 0, 0)
    Truth.assertThat(layoutChange.poll()).isNotNull()

    layoutChange.cancel()
    view.layout(0, 0, 0, 0)
    Truth.assertThat(layoutChange.poll()).isNull()
  }

  @Test @UiThreadTest
  fun layoutChangeEvents() = runBlocking<Unit> {
    val layoutChange = view.layoutChangeEvents(1)
    val oldRight = view.right
    val oldBottom = view.bottom
    view.layout(0, 0, 0, 100)
    Truth.assertThat(layoutChange.poll()).isEqualTo(
        ViewLayoutChangeEvent(
            0, 0, 0, 100,
            0, 0, oldRight, oldBottom
        )
    )

    layoutChange.cancel()
    view.layout(0, 0, 0, 0)
    Truth.assertThat(layoutChange.poll()).isNull()
  }

  @Test @UiThreadTest
  fun longClicks() = runBlocking<Unit> {
    val longClicks = view.longClicks(1)
    view.performLongClick()

    Truth.assertThat(longClicks.poll()).isNotNull()

    longClicks.cancel()
    view.performLongClick()
    Truth.assertThat(longClicks.poll()).isNull()
  }

  @Test @UiThreadTest
  fun preDraws() = runBlocking<Unit> {
    val preDraws = view.preDraws(1) { true }
    view.viewTreeObserver.dispatchOnPreDraw();
    Truth.assertThat(preDraws.poll()).isNotNull()

    preDraws.cancel()
    view.viewTreeObserver.dispatchOnPreDraw();
    Truth.assertThat(preDraws.poll()).isNull()
  }

  @Test
  fun systemUiVisibilityChanges() = runBlocking<Unit> {
    val systemUiVisibilityChanges = runBlocking(UI) {
      val systemUiVisibilityChanges = view.systemUiVisibilityChanges(1)
      rule.activity.rootView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
      systemUiVisibilityChanges
    }

    Truth.assertThat(systemUiVisibilityChanges.receive()).isEqualTo(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    runBlocking(UI) {
      rule.activity.rootView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
    Truth.assertThat(systemUiVisibilityChanges.receive()).isEqualTo(View.SYSTEM_UI_FLAG_VISIBLE)

    systemUiVisibilityChanges.cancel()
    runBlocking(UI) {
      rule.activity.rootView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    delay(100)
    Truth.assertThat(systemUiVisibilityChanges.poll()).isNull()
  }

  @Ignore("todo")
  @Test @UiThreadTest
  fun touches() = runBlocking<Unit> {
    val touches = view.touches(1)
  }

  @Ignore("todo")
  @Test @UiThreadTest
  fun keys() = runBlocking<Unit> {
    val keys = view.keys(1)
  }
}
