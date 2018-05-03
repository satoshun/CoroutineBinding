package com.github.satoshun.coroutinebinding.widget

import android.support.test.rule.ActivityTestRule
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.github.satoshun.coroutinebinding.ViewActivity
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Rule
import org.junit.Test

class CoroutineAdapterTest {
  @JvmField @Rule val rule = ActivityTestRule<ViewActivity>(ViewActivity::class.java)

  @Test
  fun dataChanges() = runBlocking<Unit> {
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
