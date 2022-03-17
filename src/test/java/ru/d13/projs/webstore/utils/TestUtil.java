package ru.d13.projs.webstore.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.google.common.io.Resources;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class TestUtil {

    public static String fileToString(String fileName) throws Exception {
        URL url = Resources.getResource(fileName);
        return Resources.toString(url, StandardCharsets.UTF_8);
    }

    public static String callSecuredResource(
            String tokenUrl
            , String requestString
            , String serviceUrl
            , MultiValueMap params
            , WebClient webClient
            ) {
        try {
            Mono<String> resource = webClient
                    .post()
                    .uri(tokenUrl)
                    .body(BodyInserters.fromFormData(params))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .flatMap(tokenResponse -> {
                        String accessTokenValue = tokenResponse.get("access_token")
                                .textValue();
                        log.info(String.format( "USED ACCESS TOKEN: \n %s", accessTokenValue ));
                        return webClient.post()
                                .uri(serviceUrl)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                                .header("Authorization", String.format("Bearer %s",accessTokenValue))
                                .body(BodyInserters.fromValue(requestString))
                                .retrieve()
                                .bodyToMono(String.class);
                    });
            String result = resource.block();
            log.info(String.format("DATA FROM SERVICE({}): \n {}"),serviceUrl,result);
            return result;
        } catch (Exception e) {
            log.error("Ошибка при вызове сервиса {}: {}", serviceUrl,e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String callResource(
            String requestString
            , String serviceUrl
            , WebClient webClient
            ) {
        try {
            Mono<String> resource = webClient.post()
                    .uri(serviceUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                    .body(BodyInserters.fromValue(requestString))
                    .retrieve()
                    .bodyToMono(String.class);
            String result = resource.block();
            log.info(String.format("DATA FROM SERVICE({}): \n {}"),serviceUrl,result);
            return result;
        } catch (Exception e) {
            log.error("Ошибка при вызове сервиса {}: {}", serviceUrl,e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
