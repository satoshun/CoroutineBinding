package com.github.satoshun.coroutinebinding.widget

import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.createListView
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.toBeCancelLaunch
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class CoroutineAdapterViewTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var spinner: Spinner
  private lateinit var listView: ListView

  @Before @UiThreadTest
  fun setUp() {
    spinner = Spinner(rule.activity)
    val values = arrayListOf("One", "Two", "Three")
    val adapter = ArrayAdapter(rule.activity, android.R.layout.simple_list_item_1, values)
    spinner.adapter = adapter
    rule.activity.view.addView(spinner)

    val p = rule.createListView()
    listView = p.first
    rule.activity.view.addView(listView)
  }

  @Test
  fun itemSelections() = testRunBlocking {
    val itemSelections = spinner.itemSelections()

    uiRunBlocking { spinner.setSelection(2) }
    itemSelections.receive().isEqualTo(2)

    uiRunBlocking { spinner.setSelection(0) }
    itemSelections.receive().isEqualTo(0)

    itemSelections.cancel()
    uiRunBlocking { spinner.setSelection(1) }
    itemSelections.poll().isNull()
  }

  @Test
  fun awaitItemSelection() = testRunBlocking {
    val job = uiLaunch {
      spinner.awaitItemSelection().isEqualTo(2)
    }
    uiRunBlocking { spinner.setSelection(2) }
    job.joinAndIsCompleted()

    val job2 = uiLaunch {
      spinner.awaitItemSelection().isEqualTo(0)
    }
    uiRunBlocking { spinner.setSelection(0) }
    job2.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch { spinner.awaitItemSelection() }
    uiRunBlocking { spinner.setSelection(1) }
    cancelJob.isCancelled.isTrue()
  }

  @Test
  fun selectionEvents() = testRunBlocking {
    val selectionEvents = listView.selectionEvents()

    uiRunBlocking { listView.setSelection(2) }
    (selectionEvents.receive() as AdapterViewItemSelectionEvent).position.isEqualTo(2)

    uiRunBlocking { listView.setSelection(0) }
    (selectionEvents.receive() as AdapterViewItemSelectionEvent).position.isEqualTo(0)

    selectionEvents.cancel()
    uiRunBlocking { listView.setSelection(1) }
    selectionEvents.poll().isNull()
  }

  @Test
  fun awaitSelectionEvent() = testRunBlocking {
    val job = uiLaunch {
      val event = listView.awaitSelectionEvent()
      (event as AdapterViewItemSelectionEvent).position.isEqualTo(2)
    }
    uiRunBlocking { listView.setSelection(2) }
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch { listView.awaitSelectionEvent() }
    uiRunBlocking { listView.setSelection(1) }
    cancelJob.isCancelled.isTrue()
  }

  @Test
  fun itemClicks() = testRunBlocking {
    val itemClicks = listView.itemClicks(1)

    uiRunBlocking { listView.performItemClick(listView.getChildAt(2), 2, 2) }
    itemClicks.receive().isEqualTo(2)

    uiRunBlocking { listView.performItemClick(listView.getChildAt(0), 0, 0) }
    itemClicks.receive().isEqualTo(0)

    itemClicks.cancel()
    uiRunBlocking { listView.performItemClick(listView.getChildAt(2), 2, 2) }
    itemClicks.poll().isNull()
  }

  @Test
  fun itemClickEvents() = testRunBlocking {
    val itemClickEvents = listView.itemClickEvents(1)

    uiRunBlocking { listView.performItemClick(listView.getChildAt(2), 2, 2) }
    itemClickEvents.receive().position.isEqualTo(2)

    uiRunBlocking { listView.performItemClick(listView.getChildAt(0), 0, 0) }
    itemClickEvents.receive().position.isEqualTo(0)

    itemClickEvents.cancel()
    uiRunBlocking { listView.performItemClick(listView.getChildAt(2), 2, 2) }
    itemClickEvents.poll().isNull()
  }

  @Test
  fun awaitItemClickEvent() = testRunBlocking {
    val job = uiLaunch { listView.awaitItemClickEvent().position.isEqualTo(2) }
    uiRunBlocking { listView.performItemClick(listView.getChildAt(2), 2, 2) }
    job.joinAndIsCompleted()

    val cancelJob = toBeCancelLaunch { listView.awaitItemClickEvent() }
    uiRunBlocking { listView.performItemClick(listView.getChildAt(2), 2, 2) }
    cancelJob.isCancelled.isTrue()
  }

  @Ignore("todo")
  @Test
  fun itemLongClicks() = testRunBlocking {
    val itemLongClicks = listView.itemLongClicks(1)
  }

  @Ignore("todo")
  @Test
  fun awaitItemLongClick() = testRunBlocking {
    listView.awaitItemLongClick()
  }

  @Ignore("todo")
  @Test
  fun itemLongClickEvents() = testRunBlocking {
    val itemLongClickEvents = listView.itemLongClickEvents(1)
  }

  @Ignore("todo")
  @Test
  fun awaitItemLongClickEvent() = testRunBlocking {
    listView.awaitItemLongClickEvent()
  }
}
