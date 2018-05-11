package com.github.satoshun.coroutinebinding.design.widget

import android.support.design.widget.TabLayout
import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import com.github.satoshun.coroutinebinding.design.ViewActivity
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isSame
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiRunBlocking
import kotlinx.coroutines.experimental.delay
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineTabLayoutTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  private lateinit var view: TabLayout
  private lateinit var tab1: TabLayout.Tab
  private lateinit var tab2: TabLayout.Tab

  @Before @UiThreadTest
  fun setUp() {
    view = TabLayout(rule.activity)
    tab1 = view.newTab()
    tab2 = view.newTab()
    tab1.select()
  }

  @Test
  fun selections() = testRunBlocking {
    val selections = uiRunBlocking { view.selections(1) }

    uiRunBlocking { tab2.select() }
    selections.receive().isSame(tab2)

    // reselection
    uiRunBlocking {
      tab2.select()
      delay(100)
    }
    selections.poll().isNull()

    selections.cancel()
    uiRunBlocking { tab1.select() }
    selections.receiveOrNull().isNull()
  }

  @Test
  fun selectionEvents() = testRunBlocking {
    val selectionEvents = uiRunBlocking { view.selectionEvents(2) }

    uiRunBlocking { tab2.select() }
    selectionEvents.receive().isEqualTo(TabLayoutSelectionUnselectedEvent(view, tab1))
    selectionEvents.receive().isEqualTo(TabLayoutSelectionSelectedEvent(view, tab2))
    // reselection
    uiRunBlocking { tab2.select() }
    selectionEvents.receiveOrNull().isEqualTo(TabLayoutSelectionReselectedEvent(view, tab2))

    selectionEvents.cancel()
    uiRunBlocking { tab1.select() }
    selectionEvents.receiveOrNull().isNull()
  }
}
