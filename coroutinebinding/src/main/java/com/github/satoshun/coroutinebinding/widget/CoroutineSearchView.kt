package com.github.satoshun.coroutinebinding.widget

import android.widget.SearchView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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
      invokeOnCloseOnMain {
        setOnQueryTextListener(null)
      }
      setOnQueryTextListener(listener)
    }

/**
 * Suspend a which emits the query text change event.
 */
suspend fun SearchView.awaitQueryTextChangeEvent(): SearchViewQueryTextEvent =
    suspendCancellableCoroutine { cont ->
      val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
          return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
          cont.resume(SearchViewQueryTextEvent(this@awaitQueryTextChangeEvent, newText, false))
          setOnQueryTextListener(null)
          return true
        }
      }
      cont.invokeOnCancellation {
        setOnQueryTextListener(null)
      }
      setOnQueryTextListener(listener)
    }

/**
 * Suspend a which emits the submit event.
 */
suspend fun SearchView.awaitSubmitQueryTextEvent(): SearchViewQueryTextEvent =
    suspendCancellableCoroutine { cont ->
      val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
          cont.resume(SearchViewQueryTextEvent(this@awaitSubmitQueryTextEvent, query, true))
          setOnQueryTextListener(null)
          return true
        }

        override fun onQueryTextChange(newText: String): Boolean {
          return false
        }
      }
      cont.invokeOnCancellation {
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
  invokeOnCloseOnMain {
    setOnQueryTextListener(null)
  }
  setOnQueryTextListener(listener)
}
