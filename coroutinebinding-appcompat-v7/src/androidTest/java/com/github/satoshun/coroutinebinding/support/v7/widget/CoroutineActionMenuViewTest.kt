package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.ActionMenuView
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isSame
import com.github.satoshun.coroutinebinding.support.v7.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineActionMenuViewTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var actionMenuView: ActionMenuView

  @Before @UiThreadTest
  fun setUp() {
    actionMenuView = ActionMenuView(rule.activity)
    rule.activity.view.addView(actionMenuView)
  }

  @Test
  fun itemClicks() = testRunBlocking {
    val (menu, item1, item2) = uiRunBlocking {
      val menu = actionMenuView.menu
      Triple(
          menu,
          menu.add(0, 1, 0, "Hi"),
          menu.add(0, 2, 0, "Hey")
      )
    }
    val itemClicks = uiRunBlocking {
      actionMenuView.itemClicks(1)
    }

    uiLaunch { menu.performIdentifierAction(2, 0) }
    itemClicks.receive().isSame(item2)

    uiLaunch { menu.performIdentifierAction(1, 0) }
    itemClicks.receive().isSame(item1)

    itemClicks.cancel()
    uiLaunch { menu.performIdentifierAction(2, 0) }
    itemClicks.receiveOrNull().isNull()
  }
}
