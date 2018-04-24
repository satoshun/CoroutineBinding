@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.widget.CompoundButton
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * todo
 */
inline fun CompoundButton.checkedChanges(): ReceiveChannel<Boolean> = cancelableChannel {
  val listener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
    safeOffer(isChecked)
  }
  onAfterClosed = {
    setOnCheckedChangeListener(null)
  }
  setOnCheckedChangeListener(listener)
}