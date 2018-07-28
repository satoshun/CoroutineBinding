package com.github.satoshun.coroutinebinding.widget

import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of scroll events on AbsListView.
 */
fun AbsListView.scrollEvents(capacity: Int = 0): ReceiveChannel<AbsListViewScrollEvent> =
    cancelableChannel(capacity) {
      val listener = object : AbsListView.OnScrollListener {
        private var currentScrollState = SCROLL_STATE_IDLE

        override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
          safeOffer(
              AbsListViewScrollEvent(
                  scrollState = currentScrollState,
                  firstVisibleItem = firstVisibleItem,
                  visibleItemCount = visibleItemCount,
                  totalItemCount = totalItemCount
              )
          )
        }

        override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
          currentScrollState = scrollState
          safeOffer(
              AbsListViewScrollEvent(
                  scrollState = currentScrollState,
                  firstVisibleItem = view.firstVisiblePosition,
                  visibleItemCount = view.childCount,
                  totalItemCount = view.count
              )
          )
        }
      }
      invokeOnCloseOnMain {
        setOnScrollListener(null)
      }
      setOnScrollListener(listener)
    }

/**
 * A scroll event on a AbsListView
 */
data class AbsListViewScrollEvent(
  val scrollState: Int,
  val firstVisibleItem: Int,
  val visibleItemCount: Int,
  val totalItemCount: Int
)
