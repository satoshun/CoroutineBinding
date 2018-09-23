package com.github.satoshun.coroutinebinding.design.widget

import androidx.annotation.CheckResult
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the offset change in view.
 */
@CheckResult
fun AppBarLayout.offsetChanges(capacity: Int = 0): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = AppBarLayout.OnOffsetChangedListener { _, offset ->
    safeOffer(offset)
  }
  invokeOnCloseOnMain {
    removeOnOffsetChangedListener(listener)
  }
  addOnOffsetChangedListener(listener)
}
