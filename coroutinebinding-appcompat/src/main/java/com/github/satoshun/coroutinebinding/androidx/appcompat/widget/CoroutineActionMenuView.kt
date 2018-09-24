package com.github.satoshun.coroutinebinding.androidx.appcompat.widget

import android.view.MenuItem
import androidx.annotation.CheckResult
import androidx.appcompat.widget.ActionMenuView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel which emits the clicked menu item in view.
 */
@CheckResult
fun ActionMenuView.itemClicks(capacity: Int = 0): ReceiveChannel<MenuItem> =
    cancelableChannel(capacity) {
      val listener = ActionMenuView.OnMenuItemClickListener {
        safeOffer(it)
        true
      }
      invokeOnCloseOnMain {
        setOnMenuItemClickListener(null)
      }
      setOnMenuItemClickListener(listener)
    }

/**
 * Suspend a which emits the clicked menu item in view.
 */
suspend fun ActionMenuView.awaitItemClick(): MenuItem =
    suspendCancellableCoroutine { cont ->
      val listener = ActionMenuView.OnMenuItemClickListener {
        cont.resume(it)
        setOnMenuItemClickListener(null)
        true
      }
      cont.invokeOnCancellation {
        setOnMenuItemClickListener(null)
      }
      setOnMenuItemClickListener(listener)
    }
