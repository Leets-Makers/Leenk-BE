package leets.leenk.domain.media.domain.service;

import leets.leenk.domain.media.application.dto.response.MediaUrlResponse;
import leets.leenk.domain.media.domain.entity.enums.DomainType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class S3PresignedUrlService {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public List<MediaUrlResponse> generateUrlList(DomainType domainType, List<String> fileNames) {
        return IntStream.range(0, fileNames.size())
                .mapToObj(i -> generateUrl(domainType, fileNames.get(i), i))
                .toList();
    }
    /*
    차후 CDN 캐싱도 고려
     */
    private MediaUrlResponse generateUrl(DomainType domainType, String fileName, int index) {
        String key = generateKey(domainType, fileName, index);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedUrlRequest = s3Presigner.presignPutObject(request);

        String putUrl = presignedUrlRequest.url().toString();

        return new MediaUrlResponse(fileName, putUrl);
    }

    private String generateKey(DomainType domainType, String fileName, int index) {
        String key = UUID.randomUUID().toString();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String filename = key + "." + extension;

        String typePath = domainType.name().toLowerCase();
        String prefix = (index == 0) ? "thumbnail_" : "";

        return String.format("originals/%s/%s%s", typePath, prefix, filename);
    }


}
