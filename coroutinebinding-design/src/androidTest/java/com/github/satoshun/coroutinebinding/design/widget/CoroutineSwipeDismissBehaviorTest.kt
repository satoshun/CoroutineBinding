package com.github.satoshun.coroutinebinding.design.widget

import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.design.ViewActivity
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.testRunBlocking
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
}
