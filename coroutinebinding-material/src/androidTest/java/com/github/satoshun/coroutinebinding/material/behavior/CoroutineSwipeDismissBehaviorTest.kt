package com.github.satoshun.coroutinebinding.material.behavior

import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.material.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.toBeCancelLaunch
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import com.google.android.material.behavior.SwipeDismissBehavior
import org.junit.Before
import org.junit.Test

class CoroutineSwipeDismissBehaviorTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var parent: CoordinatorLayout
  private lateinit var view: View

  @Before @UiThreadTest
  fun setUp() {
    parent = CoordinatorLayout(rule.activity)
    view = View(rule.activity)
    view.layoutParams = ViewGroup.LayoutParams(100, 100)
    view.id = 1
    parent.addView(view)
    rule.activity.view.addView(parent)
  }

  @Test
  fun dismisses() = testRunBlocking {
    uiRunBlocking { (view.layoutParams as CoordinatorLayout.LayoutParams).behavior = SwipeDismissBehavior<View>() }
    val dismisses = uiRunBlocking { view.dismisses(1) }

    onView(withId(1)).perform(swipeRight())
    dismisses.receive().isNotNull()

    dismisses.cancel()
    onView(withId(1)).perform(swipeRight())
    dismisses.receiveOrNull().isNull()
  }

  @Test
  fun awaitDismiss() = testRunBlocking {
    uiRunBlocking { (view.layoutParams as CoordinatorLayout.LayoutParams).behavior = SwipeDismissBehavior<View>() }

    val job = uiLaunch { view.awaitDismiss() }
    onView(withId(1)).perform(swipeRight())
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch { view.awaitDismiss() }
    onView(withId(1)).perform(swipeRight())
    cancelJob.isCancelled.isTrue()
  }
}
