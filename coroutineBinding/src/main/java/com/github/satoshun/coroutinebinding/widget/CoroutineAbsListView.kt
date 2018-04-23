@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * todo
 */
inline fun AbsListView.scrollEvents(): ReceiveChannel<AbsListViewScrollEvent> = cancelableChannel {
  val listener = object : AbsListView.OnScrollListener {
    private var currentScrollState = SCROLL_STATE_IDLE

    override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
      safeOffer(AbsListViewScrollEvent(
          scrollState = currentScrollState,
          firstVisibleItem = firstVisibleItem,
          visibleItemCount = visibleItemCount,
          totalItemCount = totalItemCount
      ))
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
      currentScrollState = scrollState
      safeOffer(AbsListViewScrollEvent(
          scrollState = currentScrollState,
          firstVisibleItem = view.firstVisiblePosition,
          visibleItemCount = view.childCount,
          totalItemCount = view.count
      ))
    }
  }
  onAfterClosed = {
    setOnScrollListener(null)
  }
  setOnScrollListener(listener)
}

data class AbsListViewScrollEvent(
    val scrollState: Int,
    val firstVisibleItem: Int,
    val visibleItemCount: Int,
    val totalItemCount: Int
)
