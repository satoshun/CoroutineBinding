@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.widget.SearchView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

inline fun SearchView.queryTextChangeEvents(): ReceiveChannel<SearchViewQueryTextEvent> = cancelableChannel {
  val listener = object : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String): Boolean {
      return safeOffer(SearchViewQueryTextEvent(this@queryTextChangeEvents, query, true))
    }

    override fun onQueryTextChange(newText: String): Boolean {
      return safeOffer(SearchViewQueryTextEvent(this@queryTextChangeEvents, newText, false))
    }
  }
  onAfterClosed = {
    setOnQueryTextListener(null)
  }
  setOnQueryTextListener(listener)
}

data class SearchViewQueryTextEvent(
  val view: SearchView,
  val queryText: CharSequence,
  val isSubmitted: Boolean
)

inline fun SearchView.queryTextChanges(): ReceiveChannel<CharSequence> = cancelableChannel {
  val listener = object : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String): Boolean {
      return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
      return safeOffer(newText)
    }
  }
  onAfterClosed = {
    setOnQueryTextListener(null)
  }
  setOnQueryTextListener(listener)
}
