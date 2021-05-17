package common

import java.util.*

class CLUtil {
    companion object {
        fun getCapitalized(data: String?): String? = data?.capitalize(Locale.ROOT)
        fun getInitials(firstName: String?, lastName: String?): String =
            "${firstName?.first()?.toUpperCase()}${
                lastName?.first()?.toUpperCase()
            }"
    }
}