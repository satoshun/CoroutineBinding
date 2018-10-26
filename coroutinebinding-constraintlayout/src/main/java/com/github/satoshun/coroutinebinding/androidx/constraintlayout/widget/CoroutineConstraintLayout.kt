package com.github.satoshun.coroutinebinding.androidx.constraintlayout.widget

import androidx.annotation.CheckResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintsChangedListener
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.channels.ReceiveChannel

/**
 * Create an channel of to emit on view constraintsChanged events.
 */
@CheckResult
fun ConstraintLayout.constraintsChanged(capacity: Int = 0): ReceiveChannel<ConstraintsChangedEvent> =
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

/**
 * A changed constraints event
 */
sealed class ConstraintsChangedEvent {
  abstract val stateId: Int
  abstract val constraintId: Int
}

/**
 * A changed constraints pre event
 */
data class ConstraintsPreChangedEvent(
  override val stateId: Int,
  override val constraintId: Int
) : ConstraintsChangedEvent()

/**
 * A changed constraints post event
 */
data class ConstraintsPostChangedEvent(
  override val stateId: Int,
  override val constraintId: Int
) : ConstraintsChangedEvent()
