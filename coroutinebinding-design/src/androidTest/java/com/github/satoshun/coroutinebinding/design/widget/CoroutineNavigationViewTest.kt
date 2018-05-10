package com.github.satoshun.coroutinebinding.design.widget

import android.support.design.widget.NavigationView
import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.v7.view.ContextThemeWrapper
import com.github.satoshun.coroutinebinding.design.R
import com.github.satoshun.coroutinebinding.design.ViewActivity
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isSame
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineNavigationViewTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var view: NavigationView

  @Before @UiThreadTest
  fun setUp() {
    val context = ContextThemeWrapper(rule.activity, R.style.Theme_AppCompat)
    view = NavigationView(context)
    rule.activity.view.addView(view)
  }

  @Test
  fun itemSelections() = testRunBlocking {
    val (menu, item1, item2) = uiRunBlocking {
      val menu = view.menu
      Triple(
          menu,
          menu.add(0, 1, 0, "Hi"),
          menu.add(0, 2, 0, "Hey")
      )
    }
    val itemSelections = uiRunBlocking { view.itemSelections(1) }

    uiRunBlocking { menu.performIdentifierAction(1, 0) }
    itemSelections.receive().isSame(item1)

    uiLaunch { menu.performIdentifierAction(2, 0) }
    itemSelections.receive().isSame(item2)

    itemSelections.cancel()
    uiLaunch { menu.performIdentifierAction(1, 0) }
    itemSelections.receiveOrNull().isNull()
  }
}
