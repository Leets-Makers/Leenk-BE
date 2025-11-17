package leets.leenk.domain.media.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaS3Service {

    private final S3PresignedUrlService s3PresignedUrlService;

    public String moveToOriginals(String tempUrl) {
        if (tempUrl == null || tempUrl.isBlank()) {
            return tempUrl;
        }

        if (tempUrl.contains("/originals/")) {
            return tempUrl;
        }

        String tempKey = extractKeyFromUrl(tempUrl);
        String originalsKey = tempKey.replace("temp/", "originals/");

        s3PresignedUrlService.copyObject(tempKey, originalsKey);
        s3PresignedUrlService.deleteObject(tempKey);

        return s3PresignedUrlService.getUrl(originalsKey);
    }

    private String extractKeyFromUrl(String url) {
        URI uri = URI.create(url);
        return uri.getPath().substring(1);
    }
}
