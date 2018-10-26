package com.github.satoshun.coroutinebinding.androidx.appcompat.widget

import androidx.annotation.CheckResult
import androidx.appcompat.widget.SearchView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Create an channel of SearchViewQueryTextEvent query text events on view.
 */
@CheckResult
fun SearchView.queryTextChangeEvents(capacity: Int = 0): ReceiveChannel<SearchViewQueryTextEvent> =
    cancelableChannel(capacity) {
      val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
          return safeOffer(
              SearchViewQueryTextEvent(
                  this@queryTextChangeEvents,
                  this@queryTextChangeEvents.query,
                  true
              )
          )
        }

        override fun onQueryTextChange(newText: String): Boolean {
          return safeOffer(
              SearchViewQueryTextEvent(
                  this@queryTextChangeEvents,
                  newText,
                  false
              )
          )
        }
      }
      invokeOnCloseOnMain {
        setOnQueryTextListener(null)
      }
      setOnQueryTextListener(listener)
    }

/**
 * Suspend a of SearchViewQueryTextEvent query text event on view.
 */
suspend fun SearchView.awaitQueryTextChangeEvent(): SearchViewQueryTextEvent =
    suspendCancellableCoroutine { cont ->
      val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
          return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
          cont.resume(
              SearchViewQueryTextEvent(
                  this@awaitQueryTextChangeEvent,
                  newText,
                  false
              )
          )
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
 * Suspend a of SearchViewQueryTextEvent query text event on view.
 */
suspend fun SearchView.awaitQueryTextSubmitEvent(): SearchViewQueryTextEvent =
    suspendCancellableCoroutine { cont ->
      val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
          cont.resume(
              SearchViewQueryTextEvent(
                  this@awaitQueryTextSubmitEvent,
                  query,
                  true
              )
          )
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
 * A query text event on a SearchView.
 */
data class SearchViewQueryTextEvent(
  val view: SearchView,
  val queryText: CharSequence,
  val isSubmitted: Boolean
)

/**
 * Create an channel of character sequences for query text changes on view.
 */
@CheckResult
fun SearchView.queryTextChange(capacity: Int = 0): ReceiveChannel<CharSequence> =
    cancelableChannel(capacity) {
      val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
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

/**
 * Suspend a of character sequences for query text changes on view.
 */
@CheckResult
suspend fun SearchView.awaitQueryTextChange(): CharSequence =
    suspendCancellableCoroutine { cont ->
      val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
          return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
          cont.resume(newText)
          setOnQueryTextListener(null)
          return true
        }
      }
      cont.invokeOnCancellation {
        setOnQueryTextListener(null)
      }
      setOnQueryTextListener(listener)
    }
