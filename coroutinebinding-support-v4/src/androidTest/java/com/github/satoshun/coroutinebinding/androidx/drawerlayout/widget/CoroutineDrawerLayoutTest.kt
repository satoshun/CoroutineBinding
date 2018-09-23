package com.github.satoshun.coroutinebinding.androidx.drawerlayout.widget

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.isFalse
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.support.v4.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CoroutineDrawerLayoutTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var main: FrameLayout
  private lateinit var drawer: FrameLayout
  private lateinit var view: DrawerLayout

  @Before @UiThreadTest
  fun setUp() {
    view = DrawerLayout(rule.activity)
    view.id = android.R.id.primary

    main = FrameLayout(rule.activity).apply {
      layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
      )
    }
    view.addView(main)

    drawer = FrameLayout(rule.activity)
    view.addView(drawer, DrawerLayout.LayoutParams(
        300,
        ViewGroup.LayoutParams.MATCH_PARENT,
        Gravity.RIGHT
    ))

    rule.activity.view.addView(view)
  }

  @Test
  fun drawerOpen() = testRunBlocking {
    val drawerOpen = uiRunBlocking { view.drawerOpen(Gravity.RIGHT, 1) }
    uiLaunch { view.openDrawer(Gravity.RIGHT) }
    drawerOpen.receive().isTrue()

    uiLaunch { view.closeDrawer(Gravity.RIGHT) }
    drawerOpen.receive().isFalse()

    drawerOpen.cancel()
    drawerOpen.receiveOrNull().isNull()
  }
}
