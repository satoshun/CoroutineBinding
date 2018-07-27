package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.annotation.CheckResult
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.github.satoshun.coroutinebinding.cancelableChannel2
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the clicked item in views menu.
 */
@CheckResult
fun Toolbar.itemClicks(capacity: Int = 0): ReceiveChannel<MenuItem> = cancelableChannel2(capacity) {
  val listener = Toolbar.OnMenuItemClickListener {
    safeOffer(it)
    true
  }
  invokeOnCloseOnMain {
    setOnMenuItemClickListener(null)
  }
  setOnMenuItemClickListener(listener)
}

/**
 * Create an channel which emits on view navigation click events.
 */
@CheckResult
fun Toolbar.navigationClicks(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel2(capacity) {
  val listener = View.OnClickListener {
    safeOffer(Unit)
  }
  invokeOnCloseOnMain {
    setNavigationOnClickListener(null)
  }
  setNavigationOnClickListener(listener)
}
