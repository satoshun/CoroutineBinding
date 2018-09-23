package com.github.satoshun.coroutinebinding.widget

import android.widget.RatingBar
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel which emits the rating change events.
 */
fun RatingBar.ratingChanges(capacity: Int = 0): ReceiveChannel<Float> = cancelableChannel(capacity) {
  val listener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
    safeOffer(rating)
  }
  invokeOnCloseOnMain {
    onRatingBarChangeListener = null
  }
  onRatingBarChangeListener = listener
}

/**
 * Suspend a which emits the rating change event.
 */
suspend fun RatingBar.awaitRatingChange(): Float = suspendCancellableCoroutine { cont ->
  val listener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
    cont.resume(rating)
    onRatingBarChangeListener = null
  }
  cont.invokeOnCancellation {
    onRatingBarChangeListener = null
  }
  onRatingBarChangeListener = listener
}

/**
 * Create an channel which emits the rating change events.
 */
fun RatingBar.ratingChangeEvents(capacity: Int = 0): ReceiveChannel<RatingBarChangeEvent> =
    cancelableChannel(capacity) {
      val listener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
        safeOffer(RatingBarChangeEvent(ratingBar, rating, fromUser))
      }
      invokeOnCloseOnMain {
        onRatingBarChangeListener = null
      }
      onRatingBarChangeListener = listener
    }

/**
 * A change event on RatingBar
 */
data class RatingBarChangeEvent(
  val view: RatingBar,
  val rating: Float,
  val fromUser: Boolean
)

/**
 * Suspend a which emits the rating change event.
 */
suspend fun RatingBar.awaitRatingChangeEvent(): RatingBarChangeEvent =
    suspendCancellableCoroutine { cont ->
      val listener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
        cont.resume(RatingBarChangeEvent(ratingBar, rating, fromUser))
        onRatingBarChangeListener = null
      }
      cont.invokeOnCancellation {
        onRatingBarChangeListener = null
      }
      onRatingBarChangeListener = listener
    }
