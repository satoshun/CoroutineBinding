package com.github.satoshun.coroutinebinding.support.v4.widget

import android.graphics.Color
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.isFalse
import com.github.satoshun.coroutinebinding.isGreaterThan
import com.github.satoshun.coroutinebinding.isLessThan
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.support.v4.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CoroutineSlidingPaneLayoutTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var view: SlidingPaneLayout

  @Before @UiThreadTest
  fun setUp() {
    view = SlidingPaneLayout(rule.activity)
    view.id = android.R.id.primary

    val paneOne = FrameLayout(rule.activity)
    val paneOneParams = ViewGroup.LayoutParams(300, MATCH_PARENT)
    view.addView(paneOne, paneOneParams)

    val paneTwo = FrameLayout(rule.activity)
    paneTwo.setBackgroundColor(Color.WHITE)
    val paneTwoParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, MATCH_PARENT)
    paneTwoParams.leftMargin = 50
    view.addView(paneTwo, paneTwoParams)

    rule.activity.view.addView(view)
  }

  @Test
  fun panelOpens() = testRunBlocking {
    val panelOpens = uiRunBlocking { view.panelOpens(1) }

    uiLaunch { view.openPane() }
    panelOpens.receive().isTrue()

    uiLaunch { view.closePane() }
    panelOpens.receive().isFalse()

    panelOpens.cancel()
    uiLaunch { view.openPane() }
    panelOpens.receiveOrNull().isNull()
  }

  @Test
  fun panelSlides() = testRunBlocking {
    val panelSlides = uiRunBlocking { view.panelSlides(1) }

    uiLaunch { view.openPane() }
    panelSlides.receive().isGreaterThan(0F)

    uiLaunch { view.closePane() }
    panelSlides.receive().isLessThan(1F)

    panelSlides.cancel()
    uiRunBlocking { view.openPane() }
    panelSlides.receiveOrNull().isNull()
  }
}
