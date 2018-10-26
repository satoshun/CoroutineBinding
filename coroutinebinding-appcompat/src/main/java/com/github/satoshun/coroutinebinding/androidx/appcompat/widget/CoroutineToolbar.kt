package com.github.satoshun.coroutinebinding.androidx.appcompat.widget

import android.view.MenuItem
import android.view.View
import androidx.annotation.CheckResult
import androidx.appcompat.widget.Toolbar
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Create an channel which emits the clicked item in views menu.
 */
@CheckResult
fun Toolbar.itemClicks(capacity: Int = 0): ReceiveChannel<MenuItem> = cancelableChannel(capacity) {
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
 * Suspend a  which emits the clicked item in views menu.
 */
@CheckResult
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
 * Create an channel which emits on view navigation click events.
 */
@CheckResult
fun Toolbar.navigationClicks(capacity: Int = 0): ReceiveChannel<Unit> = cancelableChannel(capacity) {
  val listener = View.OnClickListener {
    safeOffer(Unit)
  }
  invokeOnCloseOnMain {
    setNavigationOnClickListener(null)
  }
  setNavigationOnClickListener(listener)
}

/**
 * Suspend a  which emits on view navigation click event.
 */
@CheckResult
suspend fun Toolbar.awaitNavigationClick(): Unit = suspendCancellableCoroutine { cont ->
  val listener = View.OnClickListener {
    cont.resume(Unit)
    setNavigationOnClickListener(null)
  }
  cont.invokeOnCancellation {
    setNavigationOnClickListener(null)
  }
  setNavigationOnClickListener(listener)
}
