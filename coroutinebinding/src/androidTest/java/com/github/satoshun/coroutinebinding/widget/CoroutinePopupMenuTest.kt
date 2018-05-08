package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.widget.PopupMenu
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.testRunBlocking
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

  @Test @UiThreadTest
  fun itemClicks() = testRunBlocking {
    val menu = popupMenu.menu
    val item1 = menu.add(0, 1, 0, "Hi")
    val item2 = menu.add(0, 2, 0, "Hey")

    val itemClicks = popupMenu.itemClicks(1)

    menu.performIdentifierAction(2, 0)
    itemClicks.receive().isEqualTo(item2)

    menu.performIdentifierAction(1, 0)
    itemClicks.receive().isEqualTo(item1)

    itemClicks.cancel()
    menu.performIdentifierAction(2, 0)
    itemClicks.receiveOrNull().isNull()
  }

  @Test @UiThreadTest
  fun dismisses() = testRunBlocking {
    val dismisses = popupMenu.dismisses(1)

    popupMenu.show()
    dismisses.poll().isNull()

    popupMenu.dismiss()
    dismisses.poll().isNotNull()

    dismisses.cancel()

    popupMenu.show()
    popupMenu.dismiss()
    dismisses.poll().isNull()
  }
}
