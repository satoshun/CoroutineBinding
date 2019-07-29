package com.github.satoshun.coroutinebinding.androidx.constraintlayout.motion.widget

import androidx.annotation.CheckResult
import androidx.constraintlayout.motion.widget.MotionLayout
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.channels.ReceiveChannel

/**
 * Create an channel of to emit on view transitionChanged events.
 */
@CheckResult
fun MotionLayout.transitionChanged(capacity: Int = 0): ReceiveChannel<MotionLayoutTransition> =
  cancelableChannel(capacity) {
    val listener = object : MotionLayout.TransitionListener {
      override fun onTransitionChange(motionLayout: MotionLayout, startId: Int, endId: Int, progress: Float) {
        safeOffer(
          MotionLayoutTransitionChanged(
            motionLayout,
            startId,
            endId,
            progress
          )
        )
      }

      override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
        safeOffer(
          MotionLayoutTransitionCompleted(
            motionLayout,
            currentId
          )
        )
      }

      override fun onTransitionTrigger(motionLayout: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) {
      }

      override fun onTransitionStarted(motionLayout: MotionLayout, startId: Int, endId: Int) {
      }
    }
    invokeOnCloseOnMain {
      setTransitionListener(null)
    }
    setTransitionListener(listener)
  }

sealed class MotionLayoutTransition

data class MotionLayoutTransitionChanged(
  val view: MotionLayout,
  val startId: Int,
  val endId: Int,
  val progress: Float
) : MotionLayoutTransition()

data class MotionLayoutTransitionCompleted(
  val view: MotionLayout,
  val currentId: Int
) : MotionLayoutTransition()

data class MotionLayoutTransitionTrigger(
  val view: MotionLayout,
  val triggerId: Int,
  val positive: Boolean,
  val progress: Float
) : MotionLayoutTransition()
