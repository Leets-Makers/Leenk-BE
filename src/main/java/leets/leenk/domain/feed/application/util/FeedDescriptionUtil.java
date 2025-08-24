package leets.leenk.domain.feed.application.util;


public class FeedDescriptionUtil {
    public static String normalizeDescription(String description) {
        if (description == null) return null;
        String trimmed = description.replaceFirst("^(\r\n|\n)+", "");

        return trimmed.replaceAll("(\r\n|\n){2,}", "\n");
    }
}
