package com.github.satoshun.coroutinebinding.androidx.swiperefreshlayout.widget

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.androidx.swiperefreshlayout.ViewActivity
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CoroutineSwipeRefreshLayoutTest : AndroidTest<ViewActivity>(
    ViewActivity::class.java) {
  private lateinit var view: SwipeRefreshLayout

  @Before @UiThreadTest
  fun setUp() {
    view = SwipeRefreshLayout(rule.activity)
    view.id = 1

    val scrollView = ScrollView(rule.activity)
    view.addView(scrollView, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    scrollView.addView(FrameLayout(rule.activity), ViewGroup.LayoutParams(MATCH_PARENT, 10000000))

    rule.activity.view.addView(view)
  }

  @Test
  fun refreshes() = testRunBlocking {
    val refreshes = uiRunBlocking { view.refreshes(1) }
    onView(withId(1)).perform(swipeDown())
    refreshes.receive().isNotNull()
    uiRunBlocking { view.isRefreshing = false }

    refreshes.cancel()
    onView(withId(1)).perform(swipeDown())
    refreshes.receiveOrNull().isNull()
  }
}
