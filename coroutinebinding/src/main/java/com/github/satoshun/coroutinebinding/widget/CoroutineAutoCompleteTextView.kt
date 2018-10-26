package com.github.satoshun.coroutinebinding.widget

import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Create an channel of item click events on AutoCompleteTextView
 */
fun AutoCompleteTextView.itemClickEvents(capacity: Int = 0): ReceiveChannel<AdapterViewItemClickEvent> =
    cancelableChannel(capacity) {
      val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
        safeOffer(AdapterViewItemClickEvent(parent, view, position, id))
      }
      invokeOnCloseOnMain {
        onItemClickListener = null
      }
      onItemClickListener = listener
    }

/**
 * Suspend a item click event on AutoCompleteTextView
 */
suspend fun AutoCompleteTextView.awaitItemClickEvent(): AdapterViewItemClickEvent =
    suspendCancellableCoroutine { cont ->
      val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
        cont.resume(AdapterViewItemClickEvent(parent, view, position, id))
        onItemClickListener = null
      }
      cont.invokeOnCancellation {
        onItemClickListener = null
      }
      onItemClickListener = listener
    }
