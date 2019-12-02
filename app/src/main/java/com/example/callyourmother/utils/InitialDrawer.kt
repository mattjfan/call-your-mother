package com.example.callyourmother.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.github.stephenvinouze.shapetextdrawable.ShapeForm
import com.github.stephenvinouze.shapetextdrawable.ShapeTextDrawable

class InitialDrawer {
    companion object {
        fun getInitialDrawable(contactName: String): Drawable {
            val nameList: List<String> = contactName.split("\\s".toRegex())
            val nameListSize = nameList.size
            val firstNameChar = nameList[0][0]
            val color = "##477DAD" //TODO: link to resource @color/colorSecondary
            var strToDraw = "$firstNameChar"

            if (nameListSize > 1) {
                val lastNameChar = nameList[nameListSize - 1][0]

                strToDraw += lastNameChar
            }

            return ShapeTextDrawable(ShapeForm.SQUARE, color = Color.parseColor(color), text = strToDraw)
        }
    }
}