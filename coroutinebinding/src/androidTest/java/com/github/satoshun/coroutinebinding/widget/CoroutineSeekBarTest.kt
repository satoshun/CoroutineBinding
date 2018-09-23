package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.widget.SeekBar
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
import org.junit.Ignore
import org.junit.Test

class CoroutineSeekBarTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var seekBar: SeekBar

  @Before @UiThreadTest
  fun setUp() {
    seekBar = SeekBar(rule.activity)
    rule.activity.view.addView(seekBar)
  }

  @Test
  fun changes() = testRunBlocking {
    // todo system action

    val changes = uiRunBlocking { seekBar.changes(1) }

    uiLaunch { seekBar.progress = 85 }
    changes.receive().isEqualTo(85)

    changes.cancel()
    uiLaunch { seekBar.progress = 50 }
    changes.receiveOrNull().isNull()
  }

  @Ignore("todo")
  @Test
  fun userChanges() = testRunBlocking {
    val userChanges = uiRunBlocking { seekBar.userChanges() }
  }

  @Ignore("todo")
  @Test
  fun systemChanges() = testRunBlocking {
    val systemChanges = uiRunBlocking { seekBar.systemChanges() }
  }

  @Ignore("todo")
  @Test
  fun changeEvents() = testRunBlocking {
    val changeEvents = uiRunBlocking { seekBar.changeEvents() }
  }

  @Test
  fun awaitChange() = testRunBlocking {
    val job = uiLaunch { seekBar.awaitChange().isEqualTo(85) }
    uiLaunch { seekBar.progress = 85 }
    job.joinAndIsCompleted()

    val jobCancel = uiLaunch { seekBar.awaitChange() }
    jobCancel.cancel()
    uiRunBlocking { seekBar.progress = 50 }
    jobCancel.isCancelled.isTrue()
  }
}
