package com.github.satoshun.coroutinebinding.widget

import android.widget.SeekBar
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel which emits the progress change events.
 */
fun SeekBar.changes(capacity: Int = 0): ReceiveChannel<Int> = changes(capacity, null)

/**
 * Create an channel which emits the progress change events.
 */
fun SeekBar.userChanges(capacity: Int = 0): ReceiveChannel<Int> = changes(capacity, true)

/**
 * Create an channel which emits the progress change events.
 */
fun SeekBar.systemChanges(capacity: Int = 0): ReceiveChannel<Int> = changes(capacity, false)

/**
 * Create an channel which emits the progress change events.
 */
fun SeekBar.changes(capacity: Int = 0, shouldBeFromUser: Boolean?): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      if (shouldBeFromUser == null || shouldBeFromUser == fromUser) {
        safeOffer(progress)
      }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
  }
  invokeOnCloseOnMain {
    setOnSeekBarChangeListener(null)
  }
  setOnSeekBarChangeListener(listener)
}

/**
 * Create an channel which emits the progress change events.
 */
fun SeekBar.changeEvents(capacity: Int = 0): ReceiveChannel<SeekBarChangeEvent> = cancelableChannel(capacity) {
  val listener = object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      safeOffer(SeekBarProgressChangeEvent(seekBar, progress, fromUser))
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
      safeOffer(SeekBarStartChangeEvent(seekBar))
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
      safeOffer(SeekBarStopChangeEvent(seekBar))
    }
  }
  invokeOnCloseOnMain {
    setOnSeekBarChangeListener(null)
  }
  setOnSeekBarChangeListener(listener)
}

/**
 * A change event on SeekBar.
 */
sealed class SeekBarChangeEvent {
  abstract val view: SeekBar
}

/**
 * A progress change event on SeekBar.
 */
data class SeekBarProgressChangeEvent(
  override val view: SeekBar,
  val progress: Int,
  val fromUser: Boolean
) : SeekBarChangeEvent()

/**
 * A start change event on SeekBar.
 */
data class SeekBarStartChangeEvent(
  override val view: SeekBar
) : SeekBarChangeEvent()

/**
 * A stop change event on SeekBar.
 */
data class SeekBarStopChangeEvent(
  override val view: SeekBar
) : SeekBarChangeEvent()

/**
 * Suspend a which emits the progress change event.
 */
suspend fun SeekBar.awaitChange(): Int = awaitChange(null)

/**
 * Suspend a which emits the progress change event.
 */
suspend fun SeekBar.awaitUserChange(): Int = awaitChange(true)

/**
 * Suspend a which emits the progress change event.
 */
suspend fun SeekBar.awaitSystemChange(): Int = awaitChange(false)

/**
 * Suspend a which emits the progress change event.
 */
suspend fun SeekBar.awaitChange(shouldBeFromUser: Boolean?): Int = suspendCancellableCoroutine { cont ->
  val listener = object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      if (shouldBeFromUser == null || shouldBeFromUser == fromUser) {
        cont.resume(progress)
        setOnSeekBarChangeListener(null)
      }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
  }
  cont.invokeOnCancellation {
    setOnSeekBarChangeListener(null)
  }
  setOnSeekBarChangeListener(listener)
}

/**
 * Suspend a which emits the progress change event.
 */
suspend fun SeekBar.awaitChangeEvent(): SeekBarChangeEvent = suspendCancellableCoroutine { cont ->
  val listener = object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      cont.resume(SeekBarProgressChangeEvent(seekBar, progress, fromUser))
      setOnSeekBarChangeListener(null)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
      cont.resume(SeekBarStartChangeEvent(seekBar))
      setOnSeekBarChangeListener(null)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
      cont.resume(SeekBarStopChangeEvent(seekBar))
      setOnSeekBarChangeListener(null)
    }
  }
  cont.invokeOnCancellation {
    setOnSeekBarChangeListener(null)
  }
  setOnSeekBarChangeListener(listener)
}
