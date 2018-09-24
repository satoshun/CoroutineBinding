package com.github.satoshun.coroutinebinding.material.appbar

import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.material.R
import com.github.satoshun.coroutinebinding.material.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.toBeCancelLaunch
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import com.google.android.material.appbar.AppBarLayout
import org.junit.Before
import org.junit.Test

class CoroutineAppBarLayoutTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var parent: CoordinatorLayout
  private lateinit var view: AppBarLayout

  @Before @UiThreadTest
  fun setUp() {
    val context = ContextThemeWrapper(rule.activity, R.style.Theme_AppCompat)
    parent = CoordinatorLayout(context)
    view = AppBarLayout(context)
    parent.addView(view)
    rule.activity.view.addView(parent)
  }

  @Test
  fun offsetChanges() = testRunBlocking {
    val offsetChanges = uiRunBlocking { view.offsetChanges(1) }

    val behavior = uiRunBlocking {
      val params = view.layoutParams as CoordinatorLayout.LayoutParams
      val behavior = AppBarLayout.Behavior()
      params.behavior = behavior
      behavior.onLayoutChild(parent, view, View.LAYOUT_DIRECTION_LTR)
      behavior
    }
    offsetChanges.receive().isEqualTo(0)

    offsetChanges.cancel()
    uiRunBlocking { behavior.onLayoutChild(parent, view, View.LAYOUT_DIRECTION_LTR) }
    offsetChanges.receiveOrNull().isNull()
  }

  @Test
  fun awaitOffsetChange() = testRunBlocking {
    val job = uiLaunch {
      view.awaitOffsetChange().isEqualTo(0)
    }
    val behavior = uiRunBlocking {
      val params = view.layoutParams as CoordinatorLayout.LayoutParams
      val behavior = AppBarLayout.Behavior()
      params.behavior = behavior
      behavior.onLayoutChild(parent, view, View.LAYOUT_DIRECTION_LTR)
      behavior
    }
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch {
      view.awaitOffsetChange()
    }
    uiRunBlocking { behavior.onLayoutChild(parent, view, View.LAYOUT_DIRECTION_LTR) }
    cancelJob.isCancelled.isTrue()
  }
}
