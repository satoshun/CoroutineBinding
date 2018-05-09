package com.github.satoshun.coroutinebinding.design.widget

import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.v7.view.ContextThemeWrapper
import android.view.View
import com.github.satoshun.coroutinebinding.design.R
import com.github.satoshun.coroutinebinding.design.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineAppBarLayoutTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

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
}
