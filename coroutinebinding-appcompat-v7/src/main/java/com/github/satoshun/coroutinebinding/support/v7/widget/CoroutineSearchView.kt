@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.support.v7.widget

import android.support.annotation.CheckResult
import android.support.v7.widget.SearchView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of SearchViewQueryTextEvent query text events on view.
 */
@CheckResult
inline fun SearchView.queryTextChangeEvents(capacity: Int = 0): ReceiveChannel<SearchViewQueryTextEvent> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = object : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
      return safeOffer(SearchViewQueryTextEvent(this@queryTextChangeEvents, this@queryTextChangeEvents.query, true))
    }

    override fun onQueryTextChange(newText: String): Boolean {
      return safeOffer(SearchViewQueryTextEvent(this@queryTextChangeEvents, newText, false))
    }
  }
  onAfterClosed {
    setOnQueryTextListener(null)
  }
  setOnQueryTextListener(listener)
}

data class SearchViewQueryTextEvent(
    val view: SearchView,
    val queryText: CharSequence,
    val isSubmitted: Boolean
)

/**
 * Create an channel of character sequences for query text changes on view.
 */
@CheckResult
inline fun SearchView.queryTextChange(capacity: Int = 0): ReceiveChannel<CharSequence> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = object : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
      return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
      return safeOffer(newText)
    }
  }
  onAfterClosed {
    setOnQueryTextListener(null)
  }
  setOnQueryTextListener(listener)
}