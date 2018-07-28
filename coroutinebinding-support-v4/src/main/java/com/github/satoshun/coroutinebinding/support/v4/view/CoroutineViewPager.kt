package com.github.satoshun.coroutinebinding.support.v4.view

import android.support.annotation.CheckResult
import android.support.v4.view.ViewPager
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of scroll state change events on view.
 */
@CheckResult
fun ViewPager.pageScrollStateChanges(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = object : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {
      safeOffer(state)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }
  }
  invokeOnCloseOnMain {
    removeOnPageChangeListener(listener)
  }
  addOnPageChangeListener(listener)
}

/**
 * CreateCreate an channel of page selected events on view.
 */
@CheckResult
fun ViewPager.pageSelections(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = object : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
      safeOffer(position)
    }
  }
  invokeOnCloseOnMain {
    removeOnPageChangeListener(listener)
  }
  addOnPageChangeListener(listener)
}
