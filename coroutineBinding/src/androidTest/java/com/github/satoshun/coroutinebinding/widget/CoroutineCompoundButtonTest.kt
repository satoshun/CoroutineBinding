package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.widget.CompoundButton
import android.widget.ToggleButton
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isFalse
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.uiRunBlocking
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineCompoundButtonTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var compoundButton: CompoundButton

  @Before @UiThreadTest
  fun setUp() {
    compoundButton = ToggleButton(rule.activity)
    compoundButton.isChecked = false
    rule.activity.view.addView(compoundButton)
  }

  @Test
  fun checkedChanges() = runBlocking<Unit> {
    val checkedChanges = uiRunBlocking {
      compoundButton.checkedChanges(1)
    }

    uiRunBlocking {
      compoundButton.isChecked = true
    }
    checkedChanges.receive().isTrue()

    uiRunBlocking {
      compoundButton.isChecked = false
    }
    checkedChanges.receive().isFalse()

    checkedChanges.cancel()
    compoundButton.isChecked = false
    checkedChanges.receiveOrNull().isNull()
  }
}
