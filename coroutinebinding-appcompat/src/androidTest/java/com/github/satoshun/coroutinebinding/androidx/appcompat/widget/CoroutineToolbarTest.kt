package com.github.satoshun.coroutinebinding.androidx.appcompat.widget

import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.androidx.appcompat.R
import com.github.satoshun.coroutinebinding.androidx.appcompat.ViewActivity
import com.github.satoshun.coroutinebinding.androidx.appcompat.itemClicks
import com.github.satoshun.coroutinebinding.androidx.appcompat.navigationClicks
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isSame
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

private const val NAVIGATION_CONTENT_DESCRIPTION = "desc"

class CoroutineToolbarTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var toolbar: Toolbar

  @Before @UiThreadTest
  fun setUp() {
    val context = ContextThemeWrapper(rule.activity, R.style.Theme_AppCompat)
    toolbar = Toolbar(context)
    toolbar.navigationContentDescription = NAVIGATION_CONTENT_DESCRIPTION
    toolbar.setNavigationIcon(android.R.drawable.sym_def_app_icon)
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

  @Test
  fun navigationClicks() = testRunBlocking {
    val navigationClicks = uiRunBlocking { toolbar.navigationClicks(1) }

    onView(withContentDescription(NAVIGATION_CONTENT_DESCRIPTION)).perform(click())
    navigationClicks.receiveOrNull().isNotNull()

    onView(withContentDescription(NAVIGATION_CONTENT_DESCRIPTION)).perform(click())
    navigationClicks.receiveOrNull().isNotNull()

    navigationClicks.cancel()
    onView(withContentDescription(NAVIGATION_CONTENT_DESCRIPTION)).perform(click())
    navigationClicks.receiveOrNull().isNull()
  }
}
