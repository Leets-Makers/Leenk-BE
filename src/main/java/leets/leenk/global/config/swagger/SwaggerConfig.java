package leets.leenk.global.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import leets.leenk.global.common.exception.ApiErrorCodeExample;
import leets.leenk.global.common.exception.ErrorCodeInterface;
import leets.leenk.global.common.exception.ExampleHolder;
import leets.leenk.global.common.response.CommonResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Configuration
public class SwaggerConfig {
    private static final String JWT_SCHEME = "jwtAuth";

    public SwaggerConfig(ApplicationContext applicationContext) {
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(JWT_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                        )
                )
                .info(apiInfo());
    }

    // 스웨거 문서를 커스텀하기 위한 설정
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            // 메서드 레벨 어노테이션이 존재하는지 확인, 없으면 클래스 레벨 체크
            ApiErrorCodeExample apiErrorCodeExample = handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);
            if (apiErrorCodeExample == null) {
                apiErrorCodeExample = handlerMethod.getBeanType().getAnnotation(ApiErrorCodeExample.class);
            }

            if (apiErrorCodeExample != null) {
                for (Class<? extends ErrorCodeInterface> type : apiErrorCodeExample.value()) {
                    generateErrorCodeResponseExample(operation.getResponses(), type);
                }
            }

            return operation;
        };
    }

    // 예외 예시를 스웨거 문서에 추가하기 위한 객체를 생성하는 메서드
    private void generateErrorCodeResponseExample(ApiResponses responses, Class<? extends ErrorCodeInterface> type) {
        ErrorCodeInterface[] errorCodes = type.getEnumConstants();

        Map<Integer, List<ExampleHolder>> statusWithExampleHolders =
                Arrays.stream(errorCodes)
                        .map(errorCode -> {
                            try {
                                String enumName = ((Enum<?>) errorCode).name();

                                return ExampleHolder.builder()
                                        .holder(getSwaggerExample(errorCode.getExplainError(), errorCode))
                                        .code(errorCode.getStatus().value())
                                        .name("[" + enumName + "] " + errorCode.getMessage()) // 한글로된 드롭다운을 만들기 위해 예외 메시지를 이름으로 사용
                                        .build();
                            } catch (NoSuchFieldException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(groupingBy(ExampleHolder::getCode));

        addExamplesToResponses(responses, statusWithExampleHolders);
    }

    // ExplainError 설명과 에러코드 객체를 받아 Swagger의 Example 객체를 생성하는 메서드
    private Example getSwaggerExample(String description, ErrorCodeInterface errorCode) {
        CommonResponse<Void> errorResponse = CommonResponse.error(errorCode);
        Example example = new Example();
        example.description(description);
        example.setValue(errorResponse);

        return example;
    }

    // 스웨거의 Example 객체를 만들어 Operation.Responses에 예시 데이터를 추가하는 메서드
    private void addExamplesToResponses(ApiResponses responses, Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {
        statusWithExampleHolders.forEach((status, exampleHolders) -> {
            // ApiResponse가 없으면 생성
            ApiResponse apiResponse = responses.computeIfAbsent(String.valueOf(status), k -> new ApiResponse());

            // application/json 타입의 MediaType 가져오기 (없으면 생성)
            MediaType mediaType = getOrCreateMediaType(apiResponse);

            // 예시 데이터 추가
            exampleHolders.forEach(holder ->
                    mediaType.addExamples(holder.getName(), holder.getHolder())
            );
        });
    }

    private MediaType getOrCreateMediaType(ApiResponse apiResponse) {
        Content content = apiResponse.getContent();
        if (content == null) {
            content = new Content();
            apiResponse.setContent(content);
        }

        MediaType mediaType = content.get("application/json");
        if (mediaType == null) {
            mediaType = new MediaType();
            content.addMediaType("application/json", mediaType);
        }

        return mediaType;
    }

    private Info apiInfo() {
        return new Info()
                .title("Leenk")
                .description("Leenk API 문서")
                .version("1.1.0");
    }
}
