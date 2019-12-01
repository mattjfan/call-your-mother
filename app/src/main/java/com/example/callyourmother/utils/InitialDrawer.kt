package com.example.callyourmother.utils

import android.graphics.drawable.Drawable
import com.github.stephenvinouze.shapetextdrawable.ShapeForm
import com.github.stephenvinouze.shapetextdrawable.ShapeTextDrawable

class InitialDrawer {
    companion object {
        fun getInitialDrawable(contactName: String): Drawable {
            val nameList: List<String> = contactName.split("\\s".toRegex())
            val nameListSize = nameList.size
            val firstNameChar = nameList[0][0]
            var strToDraw = "$firstNameChar"

            if (nameListSize > 1) {
                val lastNameChar = nameList[nameListSize - 1][0]

                strToDraw += lastNameChar
            }

            return ShapeTextDrawable(ShapeForm.ROUND, text = strToDraw)
        }
    }
}