package ru.d13.projs.webstore.clients;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import static ru.d13.projs.webstore.utils.TestUtil.*;

@Slf4j
@Component
@Configuration
@PropertySource("classpath:app-props.properties")
public class ClientCalls {

    @Value(value = "${token-url}")
    private String tokenUrl;

    @Value(value = "${manager-service.clientId}")
    private String managerServiceClientId;
    @Value(value = "${manager-service.secret}")
    private String managerServiceSecret;

    // services
    @Value(value = "${service.CustomerWS.url}")
    private String customerWS_Url;


    private WebClient webClient;
    private MultiValueMap params;

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ClientCalls.class);
        ctx.refresh();
        ClientCalls mainClass = ctx.getBean(ClientCalls.class);
        mainClass.init();
        mainClass.managerServiceCallCustomerWS();
        mainClass.managerServiceCallCustomerWSDirectlyAuthError_Test();
    }

    private void init() {
        webClient = WebClient.builder().build();
    }

    @SneakyThrows
    private void managerServiceCallCustomerWS() throws Exception {
        log.info("<<<TEST1>>>");
            MultiValueMap params = new LinkedMultiValueMap() {
                {add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());}
                {add(OAuth2ParameterNames.CLIENT_ID, managerServiceClientId);}
                {add(OAuth2ParameterNames.CLIENT_SECRET, managerServiceSecret);}
            };

            String requestGetCustomers = fileToString("xml/getCustomers.xml");
            callSecuredResource(tokenUrl, requestGetCustomers, customerWS_Url,params,webClient);
    }

    @SneakyThrows
    void managerServiceCallCustomerWSDirectlyAuthError_Test() throws Exception {
        log.info("<<<TEST2>>>");
        String requestGetCustomers = fileToString("xml/getCustomers.xml");
        callResource(requestGetCustomers, customerWS_Url, webClient);
    }



}
