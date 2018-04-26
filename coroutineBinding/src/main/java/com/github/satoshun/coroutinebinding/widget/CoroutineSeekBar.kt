@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.widget.SeekBar
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * todo
 */
inline fun SeekBar.changes(): ReceiveChannel<Int> = cancelableChannel {
  val listener = object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      safeOffer(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
  }
  onAfterClosed = {
    setOnSeekBarChangeListener(null)
  }
  setOnSeekBarChangeListener(listener)
}

/**
 * todo
 */
inline fun SeekBar.userChanges(): ReceiveChannel<Int> = cancelableChannel {
  val listener = object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      if (fromUser) {
        safeOffer(progress)
      }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
  }
  onAfterClosed = {
    setOnSeekBarChangeListener(null)
  }
  setOnSeekBarChangeListener(listener)
}

/**
 * todo
 */
inline fun SeekBar.systemChanges(): ReceiveChannel<Int> = cancelableChannel {
  val listener = object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
      if (!fromUser) {
        safeOffer(progress)
      }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
  }
  onAfterClosed = {
    setOnSeekBarChangeListener(null)
  }
  setOnSeekBarChangeListener(listener)
}

/**
 * todo
 */
inline fun SeekBar.changeEvents(): ReceiveChannel<SeekBarChangeEvent> = cancelableChannel {
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
  onAfterClosed = {
    setOnSeekBarChangeListener(null)
  }
  setOnSeekBarChangeListener(listener)
}

sealed class SeekBarChangeEvent(
  open val view: SeekBar
)

data class SeekBarProgressChangeEvent(
  override val view: SeekBar,
  val progress: Int,
  val fromUser: Boolean
) : SeekBarChangeEvent(view)

data class SeekBarStartChangeEvent(
  override val view: SeekBar
) : SeekBarChangeEvent(view)

data class SeekBarStopChangeEvent(
  override val view: SeekBar
) : SeekBarChangeEvent(view)
