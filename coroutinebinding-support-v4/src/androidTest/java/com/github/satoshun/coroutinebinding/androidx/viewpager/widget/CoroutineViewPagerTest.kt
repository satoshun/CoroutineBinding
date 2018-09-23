package com.github.satoshun.coroutinebinding.androidx.viewpager.widget

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.support.v4.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CoroutineViewPagerTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var viewPager: ViewPager

  @Before @UiThreadTest
  fun setUp() {
    viewPager = ViewPager(rule.activity)
    viewPager.id = 1
    viewPager.adapter = Adapter()
    rule.activity.view.addView(viewPager)
  }

  @Test
  fun pageScrollStateChanges() = testRunBlocking {
    uiRunBlocking { viewPager.currentItem = 0 }
    val pageScrollStateChanges = uiRunBlocking { viewPager.pageScrollStateChanges(3) }

    onView(withId(1)).perform(swipeLeft())
    pageScrollStateChanges.receive().isEqualTo(ViewPager.SCROLL_STATE_DRAGGING)
    pageScrollStateChanges.receive().isEqualTo(ViewPager.SCROLL_STATE_SETTLING)
    pageScrollStateChanges.receive().isEqualTo(ViewPager.SCROLL_STATE_IDLE)

    pageScrollStateChanges.cancel()
    onView(withId(1)).perform(swipeLeft())
    pageScrollStateChanges.receiveOrNull().isNull()
  }

  @Test
  fun pageSelections() = testRunBlocking {
    uiRunBlocking { viewPager.currentItem = 0 }
    val pageSelections = uiRunBlocking { viewPager.pageSelections(1) }

    uiLaunch { viewPager.currentItem = 5 }
    pageSelections.receive().isEqualTo(5)

    pageSelections.cancel()
    uiLaunch { viewPager.currentItem = 3 }
    pageSelections.receiveOrNull().isNull()
  }
}

private class Adapter internal constructor() : PagerAdapter() {
  override fun getCount(): Int {
    return 20
  }

  override fun isViewFromObject(view: View, `object`: Any): Boolean {
    return view === `object`
  }

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    val frameLayout = FrameLayout(container.context)
    container.addView(frameLayout)
    return frameLayout
  }

  override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    container.removeView(`object` as View)
  }
}
