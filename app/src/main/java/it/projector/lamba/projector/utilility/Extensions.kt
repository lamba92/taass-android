package it.projector.lamba.projector.utilility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Che figata il Kotlin
 */
fun ViewGroup.inflate(layoutRes: Int): View? {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

@Synchronized
fun <E> ArrayList<E>.synchedAdd(item: E) {
    add(item)
}