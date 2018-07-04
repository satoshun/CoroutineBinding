@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.TextView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import com.github.satoshun.coroutinebinding.Predicate
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel of editorActions events.
 */
inline fun TextView.editorActions(capacity: Int = 0): ReceiveChannel<Int> = editorActions(capacity) { true }

/**
 * Create an channel of editorActions events.
 */
fun TextView.editorActions(capacity: Int = 0, handled: Predicate<Int>): ReceiveChannel<Int> = cancelableChannel(capacity) {
  val listener = TextView.OnEditorActionListener { _, actionId, _ ->
    if (handled(actionId)) {
      safeOffer(actionId)
    } else {
      false
    }
  }
  it {
    setOnEditorActionListener(null)
  }
  setOnEditorActionListener(listener)
}

/**
 * Create an channel of editorActions events.
 */
inline fun TextView.editorActionEvents(capacity: Int = 0): ReceiveChannel<TextViewEditorActionEvent> = editorActionEvents(capacity) { true }

/**
 * Create an channel of editorActions events.
 */
fun TextView.editorActionEvents(capacity: Int = 0, handled: Predicate<Int>): ReceiveChannel<TextViewEditorActionEvent> = cancelableChannel(capacity) {
  val listener = TextView.OnEditorActionListener { v, actionId, event ->
    if (handled(actionId)) {
      safeOffer(TextViewEditorActionEvent(v, actionId, event))
    } else {
      false
    }
  }
  it {
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
 * Create an channel of text change events.
 */
inline fun TextView.textChanges(capacity: Int = 0): ReceiveChannel<CharSequence> = cancelableChannel(capacity) {
  val listener = object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
      safeOffer(s)
    }
  }
  it {
    removeTextChangedListener(listener)
  }
  addTextChangedListener(listener)
}

/**
 * Create an channel of text change events.
 */
inline fun TextView.textChangeEvents(capacity: Int = 0): ReceiveChannel<TextViewTextChangeEvent> = cancelableChannel(capacity) {
  val listener = object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
      safeOffer(TextViewTextChangeEvent(this@textChangeEvents, s, start, before, count))
    }
  }
  it {
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
 * Create an channel of before text change events.
 */
inline fun TextView.beforeTextChangeEvents(capacity: Int = 0): ReceiveChannel<TextViewBeforeTextChangeEvent> = cancelableChannel(capacity) {
  val listener = object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
      safeOffer(TextViewBeforeTextChangeEvent(this@beforeTextChangeEvents, s, start, count, after))
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }
  }
  it {
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
 * Create an channel of after text change events.
 */
inline fun TextView.afterTextChangeEvents(capacity: Int = 0): ReceiveChannel<TextViewAfterTextChangeEvent> = cancelableChannel(capacity) {
  val listener = object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
      safeOffer(TextViewAfterTextChangeEvent(this@afterTextChangeEvents, s))
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }
  }
  it {
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
