package com.github.satoshun.coroutinebinding.widget

import android.support.annotation.RequiresApi
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel which emits the clicked item in view's menu.
 */
@RequiresApi(21)
fun Toolbar.itemClicks(capacity: Int = 0): ReceiveChannel<MenuItem> = cancelableChannel(capacity) {
  val listener = Toolbar.OnMenuItemClickListener {
    safeOffer(it)
  }
  invokeOnCloseOnMain {
    setOnMenuItemClickListener(null)
  }
  setOnMenuItemClickListener(listener)
}

/**
 * Suspend a which emits the clicked item in view's menu.
 */
@RequiresApi(21)
suspend fun Toolbar.awaitItemClick(): MenuItem = suspendCancellableCoroutine { cont ->
  val listener = Toolbar.OnMenuItemClickListener {
    cont.resume(it)
    setOnMenuItemClickListener(null)
    true
  }
  cont.invokeOnCancellation {
    setOnMenuItemClickListener(null)
  }
  setOnMenuItemClickListener(listener)
}

/**
 * Create an channel which emits on view navigation click event.
 */
@RequiresApi(21)
fun Toolbar.navigationClicks(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = View.OnClickListener {
    safeOffer(Unit)
  }
  invokeOnCloseOnMain {
    setOnClickListener(null)
  }
  setOnClickListener(listener)
}

/**
 * Suspend a which emits on view navigation click event.
 */
@RequiresApi(21)
suspend fun Toolbar.awaitNavigationClick(): Unit = suspendCancellableCoroutine { cont ->
  val listener = View.OnClickListener {
    cont.resume(Unit)
    setOnClickListener(null)
  }
  cont.invokeOnCancellation {
    setOnClickListener(null)
  }
  setOnClickListener(listener)
}
