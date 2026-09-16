package org.ferhatozcelik.util

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author ferhatozcelik
 * @since 2023-04-02
 */

class StringExtension {
    companion object {
        fun String.capitalize(): String {
            return if (isNotEmpty() && this[0].isLowerCase()) substring(
                0, 1
            ).uppercase() + substring(1) else this
        }

        fun String.htmlToString(): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
            } else {
                Html.fromHtml(this).toString()
            }

        }

        @SuppressLint("SimpleDateFormat")
        fun Date.toSimpleDate(): String? {
            return SimpleDateFormat("dd/MM/yyyy").format(this)
        }

    }
}


