package com.github.satoshun.coroutinebinding.widget

import android.widget.RadioGroup
import com.github.satoshun.coroutinebinding.cancelableChannel2
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the checked events.
 */
fun RadioGroup.checkedChanges(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel2(capacity) {
  val listener = object : RadioGroup.OnCheckedChangeListener {
    var lastChecked = -1

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
      if (lastChecked != checkedId) {
        lastChecked = checkedId
        safeOffer(checkedId)
      }
    }
  }
  invokeOnCloseOnMain {
    setOnCheckedChangeListener(null)
  }
  setOnCheckedChangeListener(listener)
}
