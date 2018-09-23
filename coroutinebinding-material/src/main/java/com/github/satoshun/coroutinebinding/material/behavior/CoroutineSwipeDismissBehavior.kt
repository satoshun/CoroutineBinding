package com.github.satoshun.coroutinebinding.material.behavior

import android.view.View
import androidx.annotation.CheckResult
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import com.google.android.material.behavior.SwipeDismissBehavior
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel which emits the dismiss events from view on SwipeDismissBehavior
 */
@CheckResult
fun View.dismisses(capacity: Int = 0): ReceiveChannel<View> = cancelableChannel(capacity) {
  val behavior = (layoutParams as CoordinatorLayout.LayoutParams).behavior as SwipeDismissBehavior

  val listener = object : SwipeDismissBehavior.OnDismissListener {
    override fun onDismiss(view: View) {
      safeOffer(view)
    }

    override fun onDragStateChanged(state: Int) {
    }
  }
  invokeOnCloseOnMain {
    behavior.setListener(null)
  }
  behavior.setListener(listener)
}

/**
 * Suspend a which emits the dismiss event from view on SwipeDismissBehavior
 */
suspend fun View.awaitDismiss(): View = suspendCancellableCoroutine { cont ->
  val behavior = (layoutParams as CoordinatorLayout.LayoutParams).behavior as SwipeDismissBehavior

  val listener = object : SwipeDismissBehavior.OnDismissListener {
    override fun onDismiss(view: View) {
      cont.resume(view)
    }

    override fun onDragStateChanged(state: Int) {
    }
  }
  cont.invokeOnCancellation {
    behavior.setListener(null)
  }
  behavior.setListener(listener)
}
