package com.github.satoshun.coroutinebinding.material.navigation

import android.view.MenuItem
import androidx.annotation.CheckResult
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.invokeOnCloseOnMain
import com.github.satoshun.coroutinebinding.safeOffer
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.experimental.channels.ReceiveChannel

/**
 * Create an channel which emits the selected item in view.
 */
@CheckResult
fun NavigationView.itemSelections(capacity: Int = 0): ReceiveChannel<MenuItem> = cancelableChannel(capacity) {
  val listener = NavigationView.OnNavigationItemSelectedListener {
    safeOffer(it)
    true
  }
  invokeOnCloseOnMain {
    setNavigationItemSelectedListener(null)
  }
  setNavigationItemSelectedListener(listener)

  // emit initial item
  val size = menu.size()
  for (i in 0 until size) {
    val item = menu.getItem(i)
    if (item.isChecked) {
      safeOffer(item)
      break
    }
  }
}
