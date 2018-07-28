package com.github.satoshun.coroutinebinding.design.widget

import android.support.annotation.CheckResult
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.SwipeDismissBehavior
import android.view.View
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

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
