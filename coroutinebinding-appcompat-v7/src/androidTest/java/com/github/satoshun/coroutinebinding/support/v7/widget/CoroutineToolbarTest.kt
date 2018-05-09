package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.Toolbar
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isSame
import com.github.satoshun.coroutinebinding.support.v7.ViewActivity
import com.github.satoshun.coroutinebinding.support.v7.appcompat.R
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineToolbarTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var toolbar: Toolbar

  @Before @UiThreadTest
  fun setUp() {
    val context = ContextThemeWrapper(rule.activity, R.style.Theme_AppCompat)
    toolbar = Toolbar(context)
    rule.activity.view.addView(toolbar)
  }

  @Test
  fun itemClicks() = testRunBlocking {
    val (menu, item1, item2) = uiRunBlocking {
      val menu = toolbar.menu
      Triple(
          menu,
          menu.add(0, 1, 0, "Hi"),
          menu.add(0, 2, 0, "Hey")
      )
    }
    val itemClicks = uiRunBlocking { toolbar.itemClicks(1) }

    uiRunBlocking { menu.performIdentifierAction(2, 0) }
    itemClicks.receive().isSame(item2)

    uiRunBlocking { menu.performIdentifierAction(1, 0) }
    itemClicks.receive().isSame(item1)

    itemClicks.cancel()
    uiRunBlocking { menu.performIdentifierAction(2, 0) }
    itemClicks.receiveOrNull().isNull()
  }
}
