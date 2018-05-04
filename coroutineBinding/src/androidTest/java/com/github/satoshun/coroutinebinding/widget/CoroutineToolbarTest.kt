package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withContentDescription
import android.support.test.rule.ActivityTestRule
import android.widget.Toolbar
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.uiRunBlocking
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val NAVIGATION_CONTENT_DESCRIPTION = "test"

class CoroutineToolbarTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var toolbar: Toolbar

  @Before @UiThreadTest
  fun setUp() {
    assumeTrue(android.os.Build.VERSION.SDK_INT >= 21)
    toolbar = Toolbar(rule.activity)
    toolbar.navigationContentDescription = NAVIGATION_CONTENT_DESCRIPTION
    toolbar.setNavigationIcon(android.R.drawable.sym_def_app_icon)
    rule.activity.view.addView(toolbar)
  }

  @Test
  fun itemClicks() = runBlocking<Unit> {
    val (menu, item1, item2) = uiRunBlocking {
      val menu = toolbar.menu
      val item1 = menu.add(0, 1, 0, "Hi")
      val item2 = menu.add(0, 2, 0, "Hey")
      Triple(menu, item1, item2)
    }

    val itemClicks = toolbar.itemClicks(1)

    menu.performIdentifierAction(2, 0)
    itemClicks.receive().isEqualTo(item2)

    menu.performIdentifierAction(1, 0)
    itemClicks.receive().isEqualTo(item1)

    itemClicks.cancel()
    menu.performIdentifierAction(2, 0)
    itemClicks.receiveOrNull().isNull()
  }

  @Test
  fun navigationClicks() = runBlocking<Unit> {
    val navigationClicks = uiRunBlocking { toolbar.navigationClicks(1) }

    onView(withContentDescription(NAVIGATION_CONTENT_DESCRIPTION))
        .perform(click())
    navigationClicks.receiveOrNull().isNotNull()

    navigationClicks.cancel()
    onView(withContentDescription(NAVIGATION_CONTENT_DESCRIPTION))
        .perform(click())
    navigationClicks.receiveOrNull().isNull()
  }
}
