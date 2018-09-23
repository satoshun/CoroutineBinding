package com.github.satoshun.coroutinebinding.androidx.constraintlayout.widget

import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.androidx.constraintlayout.R
import com.github.satoshun.coroutinebinding.androidx.constraintlayout.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Test

class CoroutineConstraintLayoutTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private val activity get() = rule.activity
  private val layout get() = activity.view

  @Test
  fun constraintsChanged() = testRunBlocking {
    val constraintsChanged = uiRunBlocking {
      layout.constraintsChanged(1)
    }

    uiLaunch {
      layout.setState(R.id.foo2, 1000, 1000)
    }
    val value = constraintsChanged.receiveOrNull()!!
    value.stateId.isEqualTo(R.id.foo2)

    constraintsChanged.cancel()
    uiLaunch {
      layout.setState(R.id.foo, 1000, 1000)
    }
    constraintsChanged.receiveOrNull().isNull()
  }
}
