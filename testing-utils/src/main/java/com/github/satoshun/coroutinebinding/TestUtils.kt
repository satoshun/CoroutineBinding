@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.test.rule.ActivityTestRule
import com.google.common.truth.Truth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass

inline fun Any?.isNull() = Truth.assertThat(this).isNull()
inline fun Any?.isNotNull() = Truth.assertThat(this).isNotNull()
inline fun Any?.isEqualTo(other: Any?) = Truth.assertThat(this).isEqualTo(other)
inline fun Any?.isInstanceOf(other: KClass<*>) = Truth.assertThat(this).isInstanceOf(other.java)
inline fun Boolean?.isTrue() = Truth.assertThat(this).isTrue()
inline fun Boolean?.isFalse() = Truth.assertThat(this).isFalse()
inline fun Float?.isGreaterThan(other: Float) = Truth.assertThat(this).isGreaterThan(other)
inline fun Float?.isLessThan(other: Float) = Truth.assertThat(this).isLessThan(other)
inline fun Any?.isSame(other: Any?) = Truth.assertThat(this).isSameAs(other)

fun testRunBlocking(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> Unit
) {
  return runBlocking(context = context, block = {
    withTimeout(5000) { block() }
  })
}

fun <T> uiRunBlocking(block: suspend CoroutineScope.() -> T): T {
  return runBlocking(context = Dispatchers.Main, block = block)
}

fun uiLaunch(block: suspend CoroutineScope.() -> Unit): Job {
  return GlobalScope.launch(context = Dispatchers.Main, block = block)
}

fun CoroutineScope.uiLaunch(block: suspend CoroutineScope.() -> Unit): Job {
  return launch(context = Dispatchers.Main, block = block)
}

fun CoroutineScope.toBeCancelLaunch(
  block: suspend CoroutineScope.() -> Unit
): Job {
  return launch(context = Dispatchers.Main, block = {
    block()
    throw Exception("should be cancel")
  }).apply {
    cancel()
  }
}

fun ActivityTestRule<out Activity>.createListView(): Pair<ListView, ListAdapter> {
  val listView = ListView(activity)
  val adapter = MyListAdapter()
  listView.adapter = adapter
  return listView to adapter
}

class MyListAdapter : BaseAdapter() {
  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    return TextView(parent.context).apply {
      text = position.toString()
    }
  }

  override fun getItem(position: Int): Any {
    return position
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun getCount(): Int = 100
}

suspend fun Job.joinAndIsCompleted() {
  join()
  isCompleted.isTrue()
}
