package com.github.satoshun.coroutinebinding.support.v4.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.v4.widget.NestedScrollView
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isSame
import com.github.satoshun.coroutinebinding.support.v4.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CortoutineNestedScrollViewTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var scrollView: ScrollView
  private lateinit var view: NestedScrollView
  private lateinit var emptyView: FrameLayout

  @Before @UiThreadTest
  fun setUp() {
    scrollView = ScrollView(rule.activity).apply {
      layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
      )
    }
    emptyView = FrameLayout(rule.activity)
    view = NestedScrollView(rule.activity)

    val emptyParams = ViewGroup.LayoutParams(50000, 50000)

    view.addView(emptyView, emptyParams)
    scrollView.addView(view, emptyParams)
    rule.activity.view.addView(scrollView)
  }

  @Test
  fun scrollChangeEvents() = testRunBlocking {
    val scrollChangeEvents = uiRunBlocking { view.scrollChangeEvents(1) }
    uiLaunch { view.scrollTo(1000, 0) }
    scrollChangeEvents.receive().also {
      it.view.isSame(view)
      it.scrollX.isEqualTo(1000)
      it.scrollY.isEqualTo(0)
      it.oldScrollX.isEqualTo(0)
      it.oldScrollY.isEqualTo(0)
    }

    scrollChangeEvents.cancel()
    uiRunBlocking { view.scrollTo(1000, 0) }
    scrollChangeEvents.receiveOrNull().isNull()
  }
}
