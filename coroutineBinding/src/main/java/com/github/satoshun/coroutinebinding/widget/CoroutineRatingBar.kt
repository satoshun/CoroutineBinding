@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.widget.RatingBar
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the rating change events.
 */
inline fun RatingBar.ratingChanges(capacity: Int = 0): ReceiveChannel<Float> = cancelableChannel(capacity) {
  val listener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
    safeOffer(rating)
  }
  it {
    onRatingBarChangeListener = null
  }
  onRatingBarChangeListener = listener
}

/**
 * Create an channel which emits the rating change events.
 */
inline fun RatingBar.ratingChangeEvents(capacity: Int = 0): ReceiveChannel<RatingBarChangeEvent> = cancelableChannel(capacity) {
  val listener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
    safeOffer(RatingBarChangeEvent(ratingBar, rating, fromUser))
  }
  it {
    onRatingBarChangeListener = null
  }
  onRatingBarChangeListener = listener
}

data class RatingBarChangeEvent(
    val view: RatingBar,
    val rating: Float,
    val fromUser: Boolean
)
