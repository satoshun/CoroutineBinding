package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.widget.SeekBar
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineSeekBarTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var seekBar: SeekBar

  @Before @UiThreadTest
  fun setUp() {
    seekBar = SeekBar(rule.activity)
    rule.activity.view.addView(seekBar)
  }

  @Test
  fun changes() = runBlocking<Unit> {
    // todo system action

    val changes = uiRunBlocking { seekBar.changes(1) }

    uiLaunch { seekBar.progress = 85 }
    changes.receive().isEqualTo(85)

    changes.cancel()
    uiLaunch { seekBar.progress = 50 }
    changes.receiveOrNull().isNull()
  }

  @Test
  fun userChanges() = runBlocking<Unit> {
    // todo
    val userChanges = uiRunBlocking { seekBar.userChanges() }
  }

  @Test
  fun systemChanges() = runBlocking<Unit> {
    // todo
    val systemChanges = uiRunBlocking { seekBar.systemChanges() }
  }

  @Test
  fun changeEvents() = runBlocking<Unit> {
    // todo
    val changeEvents = uiRunBlocking { seekBar.changeEvents() }
  }
}
