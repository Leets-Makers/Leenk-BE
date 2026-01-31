package leets.leenk.domain.feed.application.util

object FeedDescriptionUtil {
    @JvmStatic
    fun normalizeDescription(description: String?): String? {
        if (description == null) return null

        val trimmed = description.replaceFirst(Regex("^(\r\n|\n)+"), "")

        return trimmed.replace(Regex("(\r\n|\n){2,}"), "\n")
    }
}
