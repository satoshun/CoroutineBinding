package com.github.satoshun.coroutinebinding.widget

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.TextView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import com.github.satoshun.coroutinebinding.view.Predicate
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

/**
 * Create an channel of editorActions events.
 */
fun TextView.editorActions(capacity: Int = 0): ReceiveChannel<Int> = editorActions(capacity) { true }

/**
 * Create an channel of editorActions events.
 */
fun TextView.editorActions(capacity: Int = 0, handled: Predicate<Int>): ReceiveChannel<Int> =
    cancelableChannel(capacity) {
      val listener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (handled(actionId)) {
          safeOffer(actionId)
        } else {
          false
        }
      }
      invokeOnCloseOnMain {
        setOnEditorActionListener(null)
      }
      setOnEditorActionListener(listener)
    }

/**
 * Suspend a of editorActions event.
 */
suspend fun TextView.awaitEditorAction(): Int = awaitEditorAction { true }

/**
 * Suspend a of editorActions event.
 */
suspend fun TextView.awaitEditorAction(handled: Predicate<Int>): Int =
    suspendCancellableCoroutine { cont ->
      val listener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (handled(actionId)) {
          cont.resume(actionId)
          setOnEditorActionListener(null)
          true
        } else {
          false
        }
      }
      cont.invokeOnCancellation {
        setOnEditorActionListener(null)
      }
      setOnEditorActionListener(listener)
    }

/**
 * Create an channel of editorActions events.
 */
fun TextView.editorActionEvents(capacity: Int = 0): ReceiveChannel<TextViewEditorActionEvent> =
    editorActionEvents(capacity) { true }

/**
 * Create an channel of editorActions events.
 */
fun TextView.editorActionEvents(capacity: Int = 0, handled: Predicate<Int>): ReceiveChannel<TextViewEditorActionEvent> =
    cancelableChannel(capacity) {
      val listener = TextView.OnEditorActionListener { v, actionId, event ->
        if (handled(actionId)) {
          safeOffer(TextViewEditorActionEvent(v, actionId, event))
        } else {
          false
        }
      }
      invokeOnCloseOnMain {
        setOnEditorActionListener(null)
      }
      setOnEditorActionListener(listener)
    }

data class TextViewEditorActionEvent(
  val view: TextView,
  val actionId: Int,
  val keyEvent: KeyEvent?
)

/**
 * Suspend a of editorActions event.
 */
suspend fun TextView.awaitEditorActionEvent(): TextViewEditorActionEvent =
    awaitEditorActionEvent { true }

/**
 * Suspend a of editorActions event.
 */
suspend fun TextView.awaitEditorActionEvent(handled: Predicate<Int>): TextViewEditorActionEvent =
    suspendCancellableCoroutine { cont ->
      val listener = TextView.OnEditorActionListener { v, actionId, event ->
        if (handled(actionId)) {
          cont.resume(TextViewEditorActionEvent(v, actionId, event))
          setOnEditorActionListener(null)
          true
        } else {
          false
        }
      }
      cont.invokeOnCancellation {
        setOnEditorActionListener(null)
      }
      setOnEditorActionListener(listener)
    }

/**
 * Create an channel of text change events.
 */
fun TextView.textChanges(capacity: Int = 0): ReceiveChannel<CharSequence> = cancelableChannel(capacity) {
  val listener = object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
      safeOffer(s)
    }
  }
  invokeOnCloseOnMain {
    removeTextChangedListener(listener)
  }
  addTextChangedListener(listener)
}

/**
 * Suspend a of text change event.
 */
suspend fun TextView.awaitTextChange(): CharSequence = suspendCancellableCoroutine { cont ->
  val listener = object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
      cont.resume(s)
      removeTextChangedListener(this)
    }
  }
  cont.invokeOnCancellation {
    removeTextChangedListener(listener)
  }
  addTextChangedListener(listener)
}

/**
 * Create an channel of text change events.
 */
fun TextView.textChangeEvents(capacity: Int = 0): ReceiveChannel<TextViewTextChangeEvent> =
    cancelableChannel(capacity) {
      val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
          safeOffer(TextViewTextChangeEvent(this@textChangeEvents, s, start, before, count))
        }
      }
      invokeOnCloseOnMain {
        removeTextChangedListener(listener)
      }
      addTextChangedListener(listener)
    }

/**
 * A text-change event on a view.
 */
data class TextViewTextChangeEvent(
  val view: TextView,
  val text: CharSequence,
  val start: Int,
  val before: Int,
  val count: Int
)

/**
 * Suspend a of text change event.
 */
suspend fun TextView.awaitTextChangeEvent(): TextViewTextChangeEvent =
    suspendCancellableCoroutine { cont ->
      val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
          cont.resume(TextViewTextChangeEvent(this@awaitTextChangeEvent, s, start, before, count))
          removeTextChangedListener(this)
        }
      }
      cont.invokeOnCancellation {
        removeTextChangedListener(listener)
      }
      addTextChangedListener(listener)
    }

/**
 * Create an channel of before text change events.
 */
fun TextView.beforeTextChangeEvents(capacity: Int = 0): ReceiveChannel<TextViewBeforeTextChangeEvent> =
    cancelableChannel(capacity) {
      val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
          safeOffer(TextViewBeforeTextChangeEvent(this@beforeTextChangeEvents, s, start, count, after))
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }
      }
      invokeOnCloseOnMain {
        removeTextChangedListener(listener)
      }
      addTextChangedListener(listener)
    }

/**
 * A before text-change event on a view.
 */
data class TextViewBeforeTextChangeEvent(
  val view: TextView,
  val text: CharSequence,
  val start: Int,
  val count: Int,
  val after: Int
)

/**
 * Suspend a of before text change event.
 */
suspend fun TextView.awaitBeforeTextChangeEvent(): TextViewBeforeTextChangeEvent =
    suspendCancellableCoroutine { cont ->
      val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
          cont.resume(TextViewBeforeTextChangeEvent(this@awaitBeforeTextChangeEvent, s, start, count, after))
          removeTextChangedListener(this)
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }
      }
      cont.invokeOnCancellation {
        removeTextChangedListener(listener)
      }
      addTextChangedListener(listener)
    }

/**
 * Create an channel of after text change events.
 */
fun TextView.afterTextChangeEvents(capacity: Int = 0): ReceiveChannel<TextViewAfterTextChangeEvent> =
    cancelableChannel(capacity) {
      val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
          safeOffer(TextViewAfterTextChangeEvent(this@afterTextChangeEvents, s))
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }
      }
      invokeOnCloseOnMain {
        removeTextChangedListener(listener)
      }
      addTextChangedListener(listener)
    }

/**
 * An after text-change event on a view.
 */
data class TextViewAfterTextChangeEvent(
  val view: TextView,
  val editable: Editable?
)

/**
 * Suspend a of after text change event.
 */
suspend fun TextView.awaitAfterTextChangeEvent(): TextViewAfterTextChangeEvent =
    suspendCancellableCoroutine { cont ->
      val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
          cont.resume(TextViewAfterTextChangeEvent(this@awaitAfterTextChangeEvent, s))
          removeTextChangedListener(this)
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }
      }
      cont.invokeOnCancellation {
        removeTextChangedListener(listener)
      }
      addTextChangedListener(listener)
    }
