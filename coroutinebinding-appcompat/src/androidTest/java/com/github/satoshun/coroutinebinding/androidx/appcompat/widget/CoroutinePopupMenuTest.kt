package com.github.satoshun.coroutinebinding.androidx.appcompat.widget

import androidx.appcompat.widget.PopupMenu
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.androidx.appcompat.dismisses
import com.github.satoshun.coroutinebinding.androidx.appcompat.itemClicks
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isSame
import com.github.satoshun.coroutinebinding.androidx.appcompat.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CoroutinePopupMenuTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var popupMenu: PopupMenu

  @Before @UiThreadTest
  fun setUp() {
    popupMenu = PopupMenu(rule.activity, rule.activity.view)
  }

  @Test
  fun itemClicks() = testRunBlocking {
    val (menu, item1, item2) = uiRunBlocking {
      val menu = popupMenu.menu
      Triple(
          menu,
          menu.add(0, 1, 0, "Hi"),
          menu.add(0, 2, 0, "Hey")
      )
    }
    val itemClicks = uiRunBlocking { popupMenu.itemClicks(1) }

    uiLaunch { menu.performIdentifierAction(2, 0) }
    itemClicks.receive().isSame(item2)

    uiLaunch { menu.performIdentifierAction(1, 0) }
    itemClicks.receive().isSame(item1)

    itemClicks.cancel()
    uiLaunch { menu.performIdentifierAction(2, 0) }
    itemClicks.receiveOrNull().isNull()
  }

  @Test
  fun dismisses() = testRunBlocking {
    val dismisses = uiRunBlocking { popupMenu.dismisses(1) }

    uiRunBlocking { popupMenu.show() }
    dismisses.poll().isNull()

    uiRunBlocking { popupMenu.dismiss() }
    dismisses.receiveOrNull().isNotNull()

    dismisses.cancel()
    uiRunBlocking { popupMenu.show() }
    uiRunBlocking { popupMenu.dismiss() }
    dismisses.receiveOrNull().isNull()
  }
}