package leets.leenk.domain.media.application.usecase;

import leets.leenk.domain.media.application.dto.response.MediaUrlResponse;
import leets.leenk.domain.media.domain.entity.enums.ContentType;
import leets.leenk.domain.media.domain.service.S3PresignedUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaUsecase {

    private final S3PresignedUrlService s3PresignedUrlService;

    public List<MediaUrlResponse> getUrl(ContentType contentType, List<String> fileName) {

        return s3PresignedUrlService.generateUrlList(contentType, fileName);
    }
}
