package com.github.satoshun.coroutinebinding.widget

import android.widget.SearchView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the query text change events.
 */
fun SearchView.queryTextChangeEvents(capacity: Int = 0): ReceiveChannel<SearchViewQueryTextEvent> =
  cancelableChannel(capacity) {
    val listener = object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String): Boolean {
        return safeOffer(SearchViewQueryTextEvent(this@queryTextChangeEvents, query, true))
      }

      override fun onQueryTextChange(newText: String): Boolean {
        return safeOffer(SearchViewQueryTextEvent(this@queryTextChangeEvents, newText, false))
      }
    }
    it {
      setOnQueryTextListener(null)
    }
    setOnQueryTextListener(listener)
  }

/**
 * A search query event on SearchView.
 */
data class SearchViewQueryTextEvent(
  val view: SearchView,
  val queryText: CharSequence,
  val isSubmitted: Boolean
)

/**
 * Create an channel which emits the query text change events.
 */
fun SearchView.queryTextChanges(capacity: Int = 0): ReceiveChannel<CharSequence> = cancelableChannel(capacity) {
  val listener = object : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String): Boolean {
      return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
      return safeOffer(newText)
    }
  }
  it {
    setOnQueryTextListener(null)
  }
  setOnQueryTextListener(listener)
}
