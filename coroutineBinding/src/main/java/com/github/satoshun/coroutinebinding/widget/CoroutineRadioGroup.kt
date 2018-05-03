@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.widget.RadioGroup
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the checked events.
 */
inline fun RadioGroup.checkedChanges(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = object : RadioGroup.OnCheckedChangeListener {
    var lastChecked = -1

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
      if (lastChecked != checkedId) {
        lastChecked = checkedId
        safeOffer(checkedId)
      }
    }
  }
  it {
    setOnCheckedChangeListener(null)
  }
  setOnCheckedChangeListener(listener)
}
