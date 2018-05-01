@file:Suppress("NOTHING_TO_INLINE")

package com.github.satoshun.coroutinebinding.widget

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.TextView
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import com.github.satoshun.coroutinebinding.view.Predicate
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * todo
 */
inline fun TextView.editorActions(): ReceiveChannel<Int> = editorActions { true }

/**
 * todo
 */
inline fun TextView.editorActions(crossinline handled: Predicate<Int>): ReceiveChannel<Int> = cancelableChannel {
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

inline fun TextView.editorActionEvents(): ReceiveChannel<TextViewEditorActionEvent> = editorActionEvents { true }

/**
 * todo
 */
inline fun TextView.editorActionEvents(
    crossinline handled: Predicate<Int>
): ReceiveChannel<TextViewEditorActionEvent> = cancelableChannel {
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
    val keyEvent: KeyEvent
)

/**
 * todo
 */
inline fun TextView.textChanges(): ReceiveChannel<CharSequence> = cancelableChannel {
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
 * todo
 */
inline fun TextView.textChangeEvents(): ReceiveChannel<TextViewTextChangeEvent> = cancelableChannel {
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

data class TextViewTextChangeEvent(
    val view: TextView,
    val text: CharSequence,
    val start: Int,
    val before: Int,
    val count: Int
)

/**
 * todo
 */
inline fun TextView.beforeTextChangeEvents(): ReceiveChannel<TextViewBeforeTextChangeEvent> = cancelableChannel {
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

data class TextViewBeforeTextChangeEvent(
    val view: TextView,
    val text: CharSequence,
    val start: Int,
    val count: Int,
    val after: Int
)

/**
 * todo
 */
inline fun TextView.afterTextChangeEvents(): ReceiveChannel<TextViewAfterTextChangeEvent> = cancelableChannel {
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

data class TextViewAfterTextChangeEvent(
    val view: TextView,
    val editable: Editable?
)
