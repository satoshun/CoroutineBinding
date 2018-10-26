package com.github.satoshun.coroutinebinding.androidx.viewpager.widget

import androidx.annotation.CheckResult
import androidx.viewpager.widget.ViewPager
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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
 * Create an channel of page selected events on view.
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

/**
 * Suspend a of page selected event on view.
 */
suspend fun ViewPager.awaitPageSelection(): Int = suspendCancellableCoroutine { cont ->
  val listener = object : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
      cont.resume(position)
      removeOnPageChangeListener(this)
    }
  }
  cont.invokeOnCancellation {
    removeOnPageChangeListener(listener)
  }
  addOnPageChangeListener(listener)
}
