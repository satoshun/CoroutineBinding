@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.widget.RadioGroup
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

inline fun RadioGroup.checkedChanges(): ReceiveChannel<Int> = cancelableChannel {
  val listener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
    safeOffer(checkedId)
  }
  it {
    setOnCheckedChangeListener(null)
  }
  setOnCheckedChangeListener(listener)
}
