@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.widget.RatingBar
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

inline fun RatingBar.ratingChanges(): ReceiveChannel<Float> = cancelableChannel {
  val listener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
    safeOffer(rating)
  }
  it {
    onRatingBarChangeListener = null
  }
  onRatingBarChangeListener = listener
}

inline fun RatingBar.ratingChangeEvents(): ReceiveChannel<RatingBarChangeEvent> = cancelableChannel {
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
