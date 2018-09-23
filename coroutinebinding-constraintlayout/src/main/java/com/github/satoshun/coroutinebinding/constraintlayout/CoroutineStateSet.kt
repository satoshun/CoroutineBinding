package com.github.satoshun.coroutinebinding.constraintlayout

import androidx.annotation.CheckResult
import androidx.constraintlayout.widget.ConstraintsChangedListener
import androidx.constraintlayout.widget.StateSet
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of to emit on StateSet constraintsChanged events.
 */
@CheckResult
fun StateSet.constraintsChanged(capacity: Int = 0): ReceiveChannel<ConstraintsChangedEvent> =
    cancelableChannel(capacity) {
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
