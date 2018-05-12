package com.github.satoshun.coroutinebinding.support.v4.widget

import android.support.v4.widget.SlidingPaneLayout
import android.view.View
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of the open state of the pane of view
 */
fun SlidingPaneLayout.panelOpens(capacity: Int = 0): ReceiveChannel<Boolean> = cancelableChannel(capacity) { onAfterClosed ->
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

  onAfterClosed {
    setPanelSlideListener(null)
  }
  setPanelSlideListener(listener)
}

/**
 * Create an channel of the slide offset of the pane of view
 */
  fun SlidingPaneLayout.panelSlides(capacity: Int = 0): ReceiveChannel<Float> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = object : SlidingPaneLayout.PanelSlideListener {
    override fun onPanelSlide(panel: View, slideOffset: Float) {
      safeOffer(slideOffset)
    }

    override fun onPanelClosed(panel: View) {
    }

    override fun onPanelOpened(panel: View) {
    }
  }

  onAfterClosed {
    setPanelSlideListener(null)
  }
  setPanelSlideListener(listener)
}
