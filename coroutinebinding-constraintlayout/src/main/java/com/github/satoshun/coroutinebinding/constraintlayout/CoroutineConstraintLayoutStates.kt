package com.github.satoshun.coroutinebinding.constraintlayout

import android.support.annotation.CheckResult
import androidx.constraintlayout.widget.ConstraintLayoutStates
import androidx.constraintlayout.widget.ConstraintsChangedListener
import com.github.satoshun.coroutinebinding.cancelableChannel2
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of to emit on ConstraintLayoutStates constraintsChanged events.
 */
@CheckResult
fun ConstraintLayoutStates.constraintsChanged(capacity: Int = 0): ReceiveChannel<ConstraintsChangedEvent> =
    cancelableChannel2(capacity) {
      val listener = object : ConstraintsChangedListener() {
        override fun preLayoutChange(stateId: Int, constraintId: Int) {
          super.preLayoutChange(stateId, constraintId)
          safeOffer(ConstraintsPreChangedEvent(stateId, constraintId))
        }

        override fun postLayoutChange(stateId: Int, constraintId: Int) {
          super.postLayoutChange(stateId, constraintId)
          safeOffer(ConstraintsPostChangedEvent(stateId, constraintId))
        }
      }
      invokeOnCloseOnMain {
        setOnConstraintsChanged(null)
      }
      setOnConstraintsChanged(listener)
    }
