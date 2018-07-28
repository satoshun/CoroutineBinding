package com.github.satoshun.coroutinebinding.design.widget

import android.support.annotation.CheckResult
import android.support.design.widget.AppBarLayout
import com.github.satoshun.coroutinebinding.cancelableChannel2
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the offset change in view.
 */
@CheckResult
fun AppBarLayout.offsetChanges(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel2(capacity) {
  val listener = AppBarLayout.OnOffsetChangedListener { _, offset ->
    safeOffer(offset)
  }
  invokeOnCloseOnMain {
    removeOnOffsetChangedListener(listener)
  }
  addOnOffsetChangedListener(listener)
}
