package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.widget.CompoundButton
import android.widget.ToggleButton
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isFalse
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Test

class CoroutineCompoundButtonTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var compoundButton: CompoundButton

  @Before @UiThreadTest
  fun setUp() {
    compoundButton = ToggleButton(rule.activity)
    compoundButton.isChecked = false
    rule.activity.view.addView(compoundButton)
  }

  @Test
  fun checkedChanges() = testRunBlocking {
    val checkedChanges = uiRunBlocking { compoundButton.checkedChanges(1) }

    uiRunBlocking { compoundButton.isChecked = true }
    checkedChanges.receive().isTrue()

    uiRunBlocking { compoundButton.isChecked = false }
    checkedChanges.receive().isFalse()

    checkedChanges.cancel()
    uiRunBlocking { compoundButton.isChecked = false }
    checkedChanges.receiveOrNull().isNull()
  }
}
