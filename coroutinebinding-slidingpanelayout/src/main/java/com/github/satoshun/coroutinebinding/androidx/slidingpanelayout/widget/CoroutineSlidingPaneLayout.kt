package com.github.satoshun.coroutinebinding.androidx.slidingpanelayout.widget

import android.view.View
import androidx.annotation.CheckResult
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel of the open state of the pane of view
 */
@CheckResult
fun SlidingPaneLayout.panelOpens(capacity: Int = 0): ReceiveChannel<Boolean> = cancelableChannel(capacity) {
  val listener = object : SlidingPaneLayout.PanelSlideListener {
    override fun onPanelSlide(panel: View, slideOffset: Float) {
    }

    override fun onPanelClosed(panel: View) {
      safeOffer(false)
    }

    override fun onPanelOpened(panel: View) {
      safeOffer(true)
    }
  }

  invokeOnCloseOnMain {
    setPanelSlideListener(null)
  }
  setPanelSlideListener(listener)
}

/**
 * Suspend a of the open state of the pane of view
 */
suspend fun SlidingPaneLayout.awaitPanelOpen(): Boolean = suspendCancellableCoroutine { cont ->
  val listener = object : SlidingPaneLayout.PanelSlideListener {
    override fun onPanelSlide(panel: View, slideOffset: Float) {
    }

    override fun onPanelClosed(panel: View) {
      cont.resume(false)
      setPanelSlideListener(null)
    }

    override fun onPanelOpened(panel: View) {
      cont.resume(true)
      setPanelSlideListener(null)
    }
  }

  cont.invokeOnCancellation {
    setPanelSlideListener(null)
  }
  setPanelSlideListener(listener)
}

/**
 * Create an channel of the slide offset of the pane of view
 */
@CheckResult
fun SlidingPaneLayout.panelSlides(capacity: Int = 0): ReceiveChannel<Float> = cancelableChannel(capacity) {
  val listener = object : SlidingPaneLayout.PanelSlideListener {
    override fun onPanelSlide(panel: View, slideOffset: Float) {
      safeOffer(slideOffset)
    }

    override fun onPanelClosed(panel: View) {
    }

    override fun onPanelOpened(panel: View) {
    }
  }

  invokeOnCloseOnMain {
    setPanelSlideListener(null)
  }
  setPanelSlideListener(listener)
}
