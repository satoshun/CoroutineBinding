package com.github.satoshun.coroutinebinding.androidx.recyclerview.widget

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.annotation.UiThreadTest
import com.github.satoshun.coroutinebinding.AndroidTest
import com.github.satoshun.coroutinebinding.androidx.recyclerview.ViewActivity
import com.github.satoshun.coroutinebinding.isNotNull
import com.github.satoshun.coroutinebinding.isNull
import com.github.satoshun.coroutinebinding.isTrue
import com.github.satoshun.coroutinebinding.joinAndIsCompleted
import com.github.satoshun.coroutinebinding.testRunBlocking
import com.github.satoshun.coroutinebinding.toBeCancelLaunch
import com.github.satoshun.coroutinebinding.uiLaunch
import com.github.satoshun.coroutinebinding.uiRunBlocking
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

  @Test
  fun awaitDataChange() = testRunBlocking {
    val job = uiLaunch { adapter.awaitDataChange() }
    uiLaunch { adapter.notifyDataSetChanged() }
    job.joinAndIsCompleted()


    val cancelJob = toBeCancelLaunch { adapter.awaitDataChange() }
    uiRunBlocking { adapter.notifyDataSetChanged() }
    cancelJob.isCancelled.isTrue()
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
