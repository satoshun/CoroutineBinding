package com.github.satoshun.coroutinebinding.androidx.appcompat.widget

import androidx.appcompat.widget.ActionMenuView
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.androidx.appcompat.ViewActivity
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isSame
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.toBeCancelLaunch
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CoroutineActionMenuViewTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
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

  @Test
  fun awaitItemClick() = testRunBlocking {
    val (menu, item1, item2) = uiRunBlocking {
      val menu = actionMenuView.menu
      Triple(
          menu,
          menu.add(0, 1, 0, "Hi"),
          menu.add(0, 2, 0, "Hey")
      )
    }
    val job = uiLaunch {
      actionMenuView.awaitItemClick().isSame(item2)
    }
    uiLaunch { menu.performIdentifierAction(2, 0) }
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch { actionMenuView.awaitItemClick() }
    uiRunBlocking { menu.performIdentifierAction(2, 0) }
    cancelJob.isCancelled.isTrue()
  }
}
