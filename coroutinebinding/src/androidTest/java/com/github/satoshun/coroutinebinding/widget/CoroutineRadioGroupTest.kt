package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.widget.RadioButton
import android.widget.RadioGroup
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineRadioGroupTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

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
  fun checkedChanges() = runBlocking<Unit> {
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
}