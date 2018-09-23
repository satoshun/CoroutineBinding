package com.github.satoshun.coroutinebinding.widget

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
import org.junit.Test

class CoroutineAdapterTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  @Test
  fun dataChanges() = testRunBlocking {
    val testAdapter = TestAdapter()
    val dataChanges = testAdapter.dataChanges(1)

    testAdapter.notifyDataSetChanged()
    dataChanges.poll().isNotNull()

    testAdapter.notifyDataSetChanged()
    dataChanges.poll().isNotNull()

    dataChanges.cancel()
    testAdapter.notifyDataSetChanged()
    dataChanges.poll().isNull()
  }

  @Test
  fun awaitDataChange() = testRunBlocking {
    val testAdapter = TestAdapter()
    val job = uiLaunch { testAdapter.awaitDataChange() }

    uiRunBlocking { testAdapter.notifyDataSetChanged() }
    job.join()
    job.isCompleted.isTrue()

    val cancelJob = uiLaunch { testAdapter.awaitDataChange() }
    cancelJob.cancel().isTrue()
    uiRunBlocking { testAdapter.notifyDataSetChanged() }
  }
}

private class TestAdapter internal constructor() : BaseAdapter() {
  override fun getCount(): Int {
    return 0
  }

  override fun getItem(position: Int): Any? {
    return null
  }

  override fun getItemId(position: Int): Long {
    return 0
  }

  override fun getView(position: Int, convertView: View, parent: ViewGroup): View? {
    return null
  }
}
