package com.github.satoshun.coroutinebinding.widget

import android.support.test.annotation.UiThreadTest
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.createListView
import com.github.satoshun.coroutinebinding.isEqualTo
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Before
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
  fun itemLongClicks() = testRunBlocking {
    // todo
    val itemLongClicks = listView.itemLongClicks(1)
  }

  @Test
  fun itemLongClickEvents() = testRunBlocking {
    // todo
    val itemLongClickEvents = listView.itemLongClickEvents(1)
  }
}
