package com.github.satoshun.coroutinebinding.widget

import android.widget.CompoundButton
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of booleans representing the checked state of CompoundButton.
 */
fun CompoundButton.checkedChanges(capacity: Int = 0): ReceiveChannel<Boolean> = cancelableChannel(capacity) {
  val listener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
    safeOffer(isChecked)
  }
  invokeOnCloseOnMain {
    setOnCheckedChangeListener(null)
  }
  setOnCheckedChangeListener(listener)
}
