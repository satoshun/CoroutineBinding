package com.github.satoshun.coroutinebinding.widget

import android.widget.Toolbar
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.toBeCancelLaunch
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test

private const val NAVIGATION_CONTENT_DESCRIPTION = "test"

class CoroutineToolbarTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
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
  fun itemClicks() = testRunBlocking {
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
  fun awaitItemClick() = testRunBlocking {
    val (menu, item1, item2) = uiRunBlocking {
      val menu = toolbar.menu
      val item1 = menu.add(0, 1, 0, "Hi")
      val item2 = menu.add(0, 2, 0, "Hey")
      Triple(menu, item1, item2)
    }

    val job = uiLaunch { toolbar.awaitItemClick().isEqualTo(item2) }
    uiLaunch { menu.performIdentifierAction(2, 0) }
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch { toolbar.awaitItemClick() }
    menu.performIdentifierAction(2, 0)
    cancelJob.isCancelled.isTrue()
  }

  @Test
  fun navigationClicks() = testRunBlocking {
    val navigationClicks = uiRunBlocking { toolbar.navigationClicks(1) }

    onView(withContentDescription(NAVIGATION_CONTENT_DESCRIPTION))
        .perform(click())
    navigationClicks.receiveOrNull().isNotNull()

    navigationClicks.cancel()
    onView(withContentDescription(NAVIGATION_CONTENT_DESCRIPTION))
        .perform(click())
    navigationClicks.receiveOrNull().isNull()
  }

  @Test
  fun awaitNavigationClick() = testRunBlocking {
    val navigationClicks = uiLaunch {
      toolbar.awaitNavigationClick()
    }
    onView(withContentDescription(NAVIGATION_CONTENT_DESCRIPTION))
        .perform(click())
    navigationClicks.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch {
      toolbar.awaitNavigationClick()
    }
    cancelJob.cancel()
    onView(withContentDescription(NAVIGATION_CONTENT_DESCRIPTION))
        .perform(click())
    cancelJob.isCancelled.isTrue()
  }
}
