package com.google.firebase.udacity.friendlychat.components

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.google.firebase.udacity.friendlychat.R

class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = spaceHeight
            }
            bottom = spaceHeight
        }
    }
}