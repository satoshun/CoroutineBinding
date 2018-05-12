package com.github.satoshun.coroutinebinding.support.v4.widget

import android.support.v4.widget.DrawerLayout
import android.view.View
import com.github.satoshun.coroutinebinding.cancelableChannel
import com.github.satoshun.coroutinebinding.safeOffer
import kotlinx.coroutines.experimental.channels.ReceiveChannel

fun DrawerLayout.drawerOpen(gravity: Int, capacity: Int = 0): ReceiveChannel<Boolean> = cancelableChannel(capacity) { onAfterClosed ->
  val listener = object : DrawerLayout.DrawerListener {
    override fun onDrawerStateChanged(newState: Int) {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerClosed(drawerView: View) {
      if ((drawerView.layoutParams as DrawerLayout.LayoutParams).gravity == gravity) {
        safeOffer(false)
      }
    }

    override fun onDrawerOpened(drawerView: View) {
      if ((drawerView.layoutParams as DrawerLayout.LayoutParams).gravity == gravity) {
        safeOffer(true)
      }
    }
  }
  onAfterClosed {
    removeDrawerListener(listener)
  }
  addDrawerListener(listener)
}
