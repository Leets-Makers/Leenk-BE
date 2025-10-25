package leets.leenk.domain.media.application.usecase;

import leets.leenk.domain.media.application.dto.response.MediaUrlResponse;
import leets.leenk.domain.media.domain.entity.Media;
import leets.leenk.domain.media.domain.entity.enums.ContentType;
import leets.leenk.domain.media.domain.service.MediaGetService;
import leets.leenk.domain.media.domain.service.MediaUpdateService;
import leets.leenk.domain.media.domain.service.S3PresignedUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaUsecase {

    private final S3PresignedUrlService s3PresignedUrlService;
    private final MediaGetService mediaGetService;
    private final MediaUpdateService mediaUpdateService;

    public List<MediaUrlResponse> getUrl(ContentType contentType, List<String> fileName) {

        return s3PresignedUrlService.generateUrlList(contentType, fileName);
    }

    @Transactional
    public void updateThumbnailUrl(String originalUrl, String thumbnailUrl){
        Media media = mediaGetService.findByMediaUrl(originalUrl);

        mediaUpdateService.update(media, thumbnailUrl);
    }
}
