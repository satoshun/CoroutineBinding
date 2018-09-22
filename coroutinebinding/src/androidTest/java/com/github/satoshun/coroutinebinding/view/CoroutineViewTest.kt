package com.github.satoshun.coroutinebinding.view

import android.support.test.annotation.UiThreadTest
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isFalse
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Ignore
import org.junit.Test

class CoroutineViewTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private val view: ViewGroup get() = rule.activity.view

  @Test @UiThreadTest
  fun attaches() {
    val child = TextView(rule.activity)
    val attaches = child.attaches(1)
    view.addView(child)
    attaches.poll().isNotNull()
    view.removeView(child)

    attaches.cancel()
    view.addView(child)
    attaches.poll().isNull()
  }

  @Test
  fun awaitAttach() = testRunBlocking {
    val child = uiRunBlocking { TextView(rule.activity) }
    val job = uiLaunch { child.awaitAttach() }
    job.isCompleted.isFalse()
    uiRunBlocking { view.addView(child) }
    job.join()
    job.isCompleted.isTrue()

    val cancelJob = uiLaunch { child.awaitAttach() }
    cancelJob.cancel()
  }

  @Test @UiThreadTest
  fun detaches() {
    val child = TextView(rule.activity)
    val detaches = child.detaches(1)
    view.addView(child)
    view.removeView(child)
    detaches.poll().isNotNull()

    detaches.cancel()
    view.addView(child)
    view.removeView(child)
    detaches.poll().isNull()
  }

  @Test
  fun awaitDetach() = testRunBlocking {
    val child = uiRunBlocking { TextView(rule.activity) }
    val job = uiLaunch {
      child.awaitDetach()
    }
    job.isCompleted.isFalse()
    uiRunBlocking {
      view.addView(child)
      view.removeView(child)
    }
    job.join()
    job.isCompleted.isTrue()

    val cancelJob = uiLaunch { child.awaitDetach() }
    cancelJob.cancel()
  }

  @Test @UiThreadTest
  fun clicks() {
    val clicks = view.clicks(1)

    view.performClick()
    clicks.poll().isNotNull()

    clicks.cancel()
    view.performClick()
    clicks.poll().isNull()
  }

  @Test
  fun click() = testRunBlocking {
    val job = uiLaunch { view.click() }

    job.isCompleted.isFalse()
    uiRunBlocking { view.performClick() }
    job.join()
    job.isCompleted.isTrue()

    val cancelJob = uiLaunch { view.click() }
    cancelJob.cancel()
    uiRunBlocking { view.performClick() }
    cancelJob.isCancelled.isTrue()
  }

  @Ignore("todo")
  @Test
  fun drags() = testRunBlocking {
    val drags = view.drags(1)
  }

  @Ignore("todo")
  @Test
  fun drag() = testRunBlocking {
    view.drag()
  }

  @Ignore("todo")
  @Test
  fun draws() = testRunBlocking {
    val draws = view.draws(1)
  }

  @Ignore("todo")
  @Test
  fun draw() = testRunBlocking {
    view.draw()
  }

  @Test @UiThreadTest
  fun focusChanges() {
    val focus = view.focusChanges(1)
    view.isFocusable = true
    view.requestFocus()
    focus.poll().isTrue()
    view.clearFocus()
    focus.poll().isFalse()

    focus.cancel()
    view.requestFocus()
    focus.poll().isNull()
  }

  @Test
  fun focusChange() = testRunBlocking {
    val job = uiLaunch { view.focusChange().isTrue() }
    uiRunBlocking {
      view.isFocusable = true
      view.requestFocus()
    }
    job.join()
    job.isCompleted.isTrue()

    val clearJob = uiLaunch { view.focusChange().isFalse() }
    uiRunBlocking { view.clearFocus() }
    clearJob.join()

    val cancelJob = uiLaunch { view.focusChange() }
    cancelJob.cancel()
    uiRunBlocking { view.requestFocus() }
    cancelJob.isCancelled.isTrue()
  }

  @Test @UiThreadTest
  fun globalLayouts() {
    val layouts = view.globalLayouts(1)
    view.viewTreeObserver.dispatchOnGlobalLayout()
    layouts.poll().isNotNull()

    layouts.cancel()
    view.viewTreeObserver.dispatchOnGlobalLayout()
    layouts.poll().isNull()
  }

  @Test
  fun globalLayout() = testRunBlocking {
    val job = uiLaunch { view.globalLayout() }
    job.isCompleted.isFalse()
    uiRunBlocking { view.viewTreeObserver.dispatchOnGlobalLayout() }
    job.join()
    job.isCompleted.isTrue()

    val cancelJob = uiLaunch { view.globalLayout() }
    cancelJob.cancel()
    uiRunBlocking { view.viewTreeObserver.dispatchOnGlobalLayout() }
    cancelJob.isCancelled.isTrue()
  }

  @Ignore("todo")
  @Test
  fun hovers() = testRunBlocking {
    val hovers = view.hovers(1)
  }

  @Ignore("todo")
  @Test
  fun hover() = testRunBlocking {
    view.hover()
  }

  @Test @UiThreadTest
  fun layoutChanges() {
    val layoutChange = view.layoutChanges(1)
    view.layout(0, 0, 0, 0)
    layoutChange.poll().isNotNull()

    layoutChange.cancel()
    view.layout(0, 0, 0, 0)
    layoutChange.poll().isNull()
  }

  @Ignore("todo")
  @Test
  fun layoutChange() = testRunBlocking {
    val job = uiLaunch { view.layoutChange() }
    runBlocking { view.layout(0, 0, 0, 0) }
    job.join()
    job.isCompleted.isTrue()

    val cancelJob = uiLaunch { view.layoutChange() }
    cancelJob.cancel()
    uiRunBlocking { view.layout(0, 0, 0, 0) }
    cancelJob.isCancelled.isTrue()
  }

  @Test @UiThreadTest
  fun layoutChangeEvents() {
    val layoutChange = view.layoutChangeEvents(1)
    val oldRight = view.right
    val oldBottom = view.bottom
    view.layout(0, 0, 0, 100)
    layoutChange.poll().isEqualTo(
        ViewLayoutChangeEvent(
            0, 0, 0, 100,
            0, 0, oldRight, oldBottom
        )
    )

    layoutChange.cancel()
    view.layout(0, 0, 0, 0)
    layoutChange.poll().isNull()
  }

  @Test
  fun layoutChangeEvent() = testRunBlocking {
    val oldRight = uiRunBlocking { view.right }
    val oldBottom = uiRunBlocking { view.bottom }
    val job = uiLaunch {
      val event = view.layoutChangeEvent()
      event.isEqualTo(
          ViewLayoutChangeEvent(
              0, 0, 0, 100,
              0, 0, oldRight, oldBottom
          )
      )
    }
    uiRunBlocking { view.layout(0, 0, 0, 100) }
    job.join()
    job.isCompleted.isTrue()

    val cancelJob = uiLaunch { view.layoutChangeEvent() }
    cancelJob.cancel()
    uiRunBlocking { view.layout(0, 0, 0, 0) }
    cancelJob.isCancelled.isTrue()
  }

  @Test @UiThreadTest
  fun longClicks() {
    val longClicks = view.longClicks(1)
    view.performLongClick()

    longClicks.poll().isNotNull()

    longClicks.cancel()
    view.performLongClick()
    longClicks.poll().isNull()
  }

  @Test
  fun longClick() = testRunBlocking {
    val job = uiLaunch { view.longClick() }
    job.isCompleted.isFalse()
    uiRunBlocking { view.performLongClick() }
    job.join()
    job.isCompleted.isTrue()

    val cancelJob = uiLaunch { view.longClick() }
    cancelJob.cancel()
    uiRunBlocking { view.performLongClick() }
    cancelJob.isCancelled.isTrue()
  }

  @Test @UiThreadTest
  fun preDraws() {
    val preDraws = view.preDraws(1) { true }
    view.viewTreeObserver.dispatchOnPreDraw()
    preDraws.poll().isNotNull()

    preDraws.cancel()
    view.viewTreeObserver.dispatchOnPreDraw()
    preDraws.poll().isNull()
  }

  @Test
  fun preDraw() = testRunBlocking {
    val job = uiLaunch { view.preDraw { true } }
    job.isCompleted.isFalse()
    uiRunBlocking { view.viewTreeObserver.dispatchOnPreDraw() }
    job.join()
    job.isCompleted.isTrue()

    val cancelJob = uiLaunch { view.preDraw { true } }
    cancelJob.cancel()
    uiRunBlocking { view.viewTreeObserver.dispatchOnPreDraw() }
    cancelJob.isCancelled.isTrue()
  }

  @Ignore("Flaky")
  @Test
  fun systemUiVisibilityChanges() = testRunBlocking {
    val view = view
    val systemUiVisibilityChanges = uiRunBlocking { view.systemUiVisibilityChanges() }

    uiLaunch { view.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION }
    systemUiVisibilityChanges.receive().isEqualTo(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    uiLaunch { view.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE }
    systemUiVisibilityChanges.receive().isEqualTo(View.SYSTEM_UI_FLAG_VISIBLE)

    systemUiVisibilityChanges.cancel()
    uiLaunch { view.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION }
    systemUiVisibilityChanges.receiveOrNull().isNull()
  }

  @Ignore("Flaky")
  @Test
  fun systemUiVisibilityChange() = testRunBlocking {
    val view = view
    val job = uiLaunch { view.systemUiVisibilityChange().isEqualTo(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) }

    uiRunBlocking { view.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION }
    job.join()
    job.isCompleted.isTrue()

    val job2 = uiLaunch { view.systemUiVisibilityChange().isEqualTo(View.SYSTEM_UI_FLAG_VISIBLE) }
    uiRunBlocking { view.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE }
    job2.join()
    job2.isCompleted.isTrue()

    val cancelJob = uiLaunch { view.systemUiVisibilityChange() }
    cancelJob.cancel()
    uiRunBlocking { view.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION }
    cancelJob.isCancelled.isTrue()
  }

  @Ignore("todo")
  @Test
  fun touches() = testRunBlocking {
    val touches = view.touches(1)
  }

  @Test
  fun keys() = testRunBlocking {
    val editView = uiRunBlocking {
      EditText(rule.activity).also {
        view.addView(it)
      }
    }
    val keys = uiRunBlocking {
      editView.keys(1).also {
        editView.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_R))
      }
    }
    val event = keys.receive()
    event.action.isEqualTo(KeyEvent.ACTION_DOWN)
    event.keyCode.isEqualTo(KeyEvent.KEYCODE_R)

    keys.cancel()
    uiRunBlocking {
      editView.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_R))
    }
    keys.poll().isNull()
  }
}
