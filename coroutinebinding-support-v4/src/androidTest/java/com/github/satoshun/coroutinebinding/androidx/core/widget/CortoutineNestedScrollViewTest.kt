package com.github.satoshun.coroutinebinding.androidx.core.widget

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isSame
import com.github.satoshun.coroutinebinding.support.v4.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CortoutineNestedScrollViewTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var view: NestedScrollView
  private lateinit var emptyView: FrameLayout

  @Before @UiThreadTest
  fun setUp() {
    val scrollView = ScrollView(rule.activity)
    emptyView = FrameLayout(rule.activity)
    view = NestedScrollView(rule.activity)

    val emptyParams = ViewGroup.LayoutParams(50000, 50000)

    view.addView(emptyView, emptyParams)
    scrollView.addView(view, ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    ))
    rule.activity.view.addView(scrollView, ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    ))
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
