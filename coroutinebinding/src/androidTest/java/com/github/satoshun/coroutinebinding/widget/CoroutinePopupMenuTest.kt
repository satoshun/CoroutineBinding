package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.widget.PopupMenu
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isSame
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutinePopupMenuTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var popupMenu: PopupMenu

  @Before @UiThreadTest
  fun setUp() {
    popupMenu = PopupMenu(rule.activity, rule.activity.view)
  }

  @Test
  fun itemClicks() = testRunBlocking {
    val menu = popupMenu.menu
    val item1 = uiRunBlocking { menu.add(0, 1, 0, "Hi") }
    val item2 = uiRunBlocking { menu.add(0, 2, 0, "Hey") }

    val itemClicks = uiRunBlocking { popupMenu.itemClicks(1) }

    uiRunBlocking { menu.performIdentifierAction(2, 0) }
    itemClicks.receive().isSame(item2)

    uiRunBlocking { menu.performIdentifierAction(1, 0) }
    itemClicks.receive().isSame(item1)

    itemClicks.cancel()
    uiRunBlocking { menu.performIdentifierAction(2, 0) }
    itemClicks.receiveOrNull().isNull()
  }

  @Test
  fun dismisses() = testRunBlocking {
    val dismisses = uiRunBlocking { popupMenu.dismisses(1) }
    val menu = popupMenu.menu
    menu.add(0, 1, 0, "Hi")
    menu.add(0, 2, 0, "Hey")

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
