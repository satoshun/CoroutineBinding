package com.github.satoshun.coroutinebinding.widget

import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CoroutineRadioGroupTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var radioGroup: RadioGroup

  @Before @UiThreadTest
  fun setUp() {
    radioGroup = RadioGroup(rule.activity)
    val button1 = RadioButton(rule.activity)
    button1.id = 1
    val button2 = RadioButton(rule.activity)
    button2.id = 2
    radioGroup.addView(button1)
    radioGroup.addView(button2)
    rule.activity.view.addView(radioGroup)
  }

  @Test
  fun checkedChanges() = testRunBlocking {
    val checkedChanges = uiRunBlocking {
      radioGroup.checkedChanges()
    }

    uiLaunch { radioGroup.check(1) }
    checkedChanges.receive().isEqualTo(1)

    uiLaunch { radioGroup.clearCheck() }
    checkedChanges.receive().isEqualTo(-1)

    uiLaunch { radioGroup.check(2) }
    checkedChanges.receive().isEqualTo(2)

    checkedChanges.cancel()
    uiLaunch { radioGroup.check(1) }
    checkedChanges.receiveOrNull().isNull()
  }

  @Test
  fun awaitCheckedChange() = testRunBlocking {
    val job = uiLaunch { radioGroup.awaitCheckedChange().isEqualTo(1) }
    uiLaunch { radioGroup.check(1) }
    job.joinAndIsCompleted()

    val cancelJob = uiLaunch { radioGroup.awaitCheckedChange() }
    cancelJob.cancel()
    uiRunBlocking { radioGroup.check(1) }
    cancelJob.isCancelled.isTrue()
  }
}
