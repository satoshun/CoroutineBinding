package com.github.satoshun.coroutinebinding.widget

import android.support.annotation.RequiresApi
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the clicked item in view's menu.
 */
@RequiresApi(21)
fun Toolbar.itemClicks(capacity: Int = 0): ReceiveChannel<MenuItem> = cancelableChannel(capacity) {
  val listener = Toolbar.OnMenuItemClickListener {
    safeOffer(it)
  }
  it {
    setOnMenuItemClickListener(null)
  }
  setOnMenuItemClickListener(listener)
}

/**
 * Create an channel which emits on view navigation click events.
 */
@RequiresApi(21)
fun Toolbar.navigationClicks(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = View.OnClickListener {
    safeOffer(Unit)
  }
  it {
    setOnClickListener(null)
  }
  setOnClickListener(listener)
}
