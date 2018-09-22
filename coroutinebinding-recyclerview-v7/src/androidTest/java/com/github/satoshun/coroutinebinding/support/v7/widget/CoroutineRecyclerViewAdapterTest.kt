package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.test.annotation.UiThreadTest
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.ViewGroup
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.support.v7.ViewActivity
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.uiLaunch
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test

class CoroutineRecyclerViewAdapterTest : AndroidTest<ViewActivity>(ViewActivity::class.java) {
  private lateinit var adapter: TestRecyclerAdapter
  private lateinit var view: RecyclerView

  @Before @UiThreadTest
  fun setUp() {
    adapter = TestRecyclerAdapter()
    view = RecyclerView(rule.activity)
    view.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    view.adapter = adapter
    view.layoutManager = LinearLayoutManager(rule.activity)
    view.id = android.R.id.primary
    rule.activity.view.addView(view)
  }

  @Test
  fun dataChanges() = testRunBlocking {
    val dataChanges = runBlocking { adapter.dataChanges(1) }

    uiLaunch { adapter.notifyDataSetChanged() }
    dataChanges.receiveOrNull().isNotNull()

    uiLaunch { adapter.notifyDataSetChanged() }
    dataChanges.receiveOrNull().isNotNull()

    dataChanges.cancel()
    uiLaunch { adapter.notifyDataSetChanged() }
    dataChanges.receiveOrNull().isNull()
  }
}

private class TestRecyclerAdapter internal constructor() : RecyclerView.Adapter<ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return object : ViewHolder(parent) {}
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

  override fun getItemCount(): Int {
    return 0
  }
}
