package com.github.satoshun.coroutinebinding.design.widget

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.SwipeDismissBehavior
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.swipeRight
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.view.View
import android.view.ViewGroup
import com.github.satoshun.coroutinebinding.design.ViewActivity
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineSwipeDismissBehaviorTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

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
