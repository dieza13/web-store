package ru.d13.projs.webstore.clients;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static ru.d13.projs.webstore.utils.TestUtil.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:app-props.properties")
@Testcontainers
public class ClientCallsInTestContainerTest {

    @Value(value = "${token-realm}")
    private String tokenRealm;
    private String tokenUrl;
    //// CLIENT-SERVICE
    @Value(value = "${manager-service.clientId}")
    private String managerServiceClientId;
    @Value(value = "${manager-service.secret}")
    private String managerServiceSecret;
    /////
    @Value(value = "${user.clientId}")
    private String userClientId;
    // USER1
    @Value(value = "${user1.login}")
    private String user1Login;
    @Value(value = "${user1.pass}")
    private String user1Pass;
    // USER2
    @Value(value = "${user2.login}")
    private String user2Login;
    @Value(value = "${user2.pass}")
    private String user2Pass;

    ///// services
    @Value(value = "${service.CustomerWS.url}")
    private String customerWS_Url;
    @Value(value = "${service.OrderWS.url}")
    private String orderWS_Url;
    @Value(value = "${service.ProductWS.url}")
    private String productWS_Url;


    private static WebClient webClient;
    private MultiValueMap params;

    @Container
    private static final KeycloakContainer keycloak = new KeycloakContainer("jboss/keycloak:9.0.0")
            .withRealmImportFile("cerberus-realm.json")
            .withAdminUsername("admin")
            .withAdminPassword("admin")
            ;

    private String authServerUrl;

    @BeforeAll
    public static void init() {
        webClient = WebClient.builder().build();
        log.info("<<<________________________________________________________________________>>>");
    }


    @BeforeEach
    void setup() {
        tokenUrl = String.format("%s%s",keycloak.getAuthServerUrl(),tokenRealm);
        System.out.println();
    }

    @SneakyThrows
    @DisplayName("Вызов сервиса по клиентам сервисом manager-service c авторизацией по clientId/secret")
    @Test
    void managerServiceCallCustomerWS_Test() throws Exception {
        log.info("<<<Вызов сервиса по клиентам сервисом manager-service c авторизацией по clientId/secret>>>");
        MultiValueMap params = new LinkedMultiValueMap() {
            {add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());}
            {add(OAuth2ParameterNames.CLIENT_ID, managerServiceClientId);}
            {add(OAuth2ParameterNames.CLIENT_SECRET, managerServiceSecret);}
        };

        String requestGetCustomers = fileToString("xml/getCustomers.xml");
        String response = callSecuredResource(tokenUrl, requestGetCustomers, customerWS_Url, params, webClient);
        Assert.assertTrue(response.contains("getCustomersResponse"));
        log.info("<<<________________________________________________________________________>>>");
    }

    @SneakyThrows
    @DisplayName("Ошибка при вызове сервиса по клиентам сервисом manager-service без параметров аутентификации")
    @Test
    void managerServiceCallCustomerWSDirectlyAuthError_Test() throws Exception {
        log.info("<<<Ошибка при вызове сервиса по клиентам сервисом manager-service параметров аутентификации>>>");
        String requestGetCustomers = fileToString("xml/getCustomers.xml");
        Assert.assertThrows(RuntimeException.class,()->callResource(requestGetCustomers, customerWS_Url, webClient));
        log.info("<<<________________________________________________________________________>>>");
    }

    @SneakyThrows
    @DisplayName("Вызов сервиса по заказам через user api c аутентификацией по login/password, но для пользователя без роли ROLE_CUSTOMER")
    @Test
    void userApiServiceCallOrderWSWithLoginPassWithoutRole_Test() throws Exception {
        log.info("<<<Вызов сервиса по заказам через user api c аутентификацией по login/password, но для пользователя без роли ROLE_CUSTOMER>>>");
//        /// обращение первого клиента
        MultiValueMap params = new LinkedMultiValueMap() {
            {add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue());}
            {add(OAuth2ParameterNames.CLIENT_ID, userClientId);}
            {add(OAuth2ParameterNames.USERNAME, user1Login);}
            {add(OAuth2ParameterNames.PASSWORD, user1Pass);}
        };

        String requestGetCustomers = fileToString("xml/getOrderByCode_111.xml");
        Assert.assertThrows(RuntimeException.class,()->callSecuredResource(tokenUrl, requestGetCustomers, orderWS_Url,params,webClient));
        log.info("<<<________________________________________________________________________>>>");
    }
    @SneakyThrows
    @DisplayName("Вызов сервиса по заказам через user api c аутентификацией по login/password и авторизацией по роли ROLE_CUSTOMER")
    @Test
    void userApiServiceCallOrderWSWithLoginPassWithRole_Test() throws Exception {
        log.info("<<<Вызов сервиса по заказам через user api c аутентификацией по login/password и авторизацией по роли ROLE_CUSTOMER>>>");
        /// обращение второго клиента
        params = new LinkedMultiValueMap() {
            {add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue());}
            {add(OAuth2ParameterNames.CLIENT_ID, userClientId);}
            {add(OAuth2ParameterNames.USERNAME, user2Login);}
            {add(OAuth2ParameterNames.PASSWORD, user2Pass);}
        };

        String requestGetCustomers = fileToString("xml/getOrderByCode_222.xml");
        String response = callSecuredResource(tokenUrl, requestGetCustomers, orderWS_Url,params,webClient);
        Assert.assertTrue(response.contains("getOrderByCodeResponse"));
        Assert.assertTrue(response.contains("КОД_МАЧЕТЕ"));
        log.info("<<<________________________________________________________________________>>>");
    }

    @SneakyThrows
    @DisplayName("Ошибка при вызове сервиса по заказам через user api c неправильным паролем")
    @Test
    void userApiServiceCallOrderWSWithBadPass_Test() throws Exception {
        log.info("<<<Ошибка при вызове сервиса по заказам через user api c неправильным паролем>>>");
        /// обращение первого клиента
        MultiValueMap params = new LinkedMultiValueMap() {
            {add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue());}
            {add(OAuth2ParameterNames.CLIENT_ID, userClientId);}
            {add(OAuth2ParameterNames.USERNAME, user1Login);}
            {add(OAuth2ParameterNames.PASSWORD, "неправильный пароль");}
        };

        String requestGetCustomers = fileToString("xml/getOrderByCode_111.xml");
        Assert.assertThrows(RuntimeException.class, ()->callSecuredResource(tokenUrl, requestGetCustomers, orderWS_Url,params,webClient));
        log.info("<<<________________________________________________________________________>>>");
    }

    @SneakyThrows
    @DisplayName("Вызов сервиса по клиентам через user api c авторизацией по login/password")
    @Test
    void userApiServiceCallCustomerWS_Test() throws Exception {
        log.info("<<<Вызов сервиса по клиентам через user api c авторизацией по login/password");
        MultiValueMap params = new LinkedMultiValueMap() {
            {add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue());}
            {add(OAuth2ParameterNames.CLIENT_ID, userClientId);}
            {add(OAuth2ParameterNames.USERNAME, user2Login);}
            {add(OAuth2ParameterNames.PASSWORD, user2Pass);}
        };

        String requestGetCustomers = fileToString("xml/getCustomers.xml");
        String response = callSecuredResource(tokenUrl, requestGetCustomers, customerWS_Url, params, webClient);
        Assert.assertTrue(response.contains("getCustomersResponse"));
        log.info("<<<________________________________________________________________________>>>");
    }

    @SneakyThrows
    @DisplayName("Вызов сервиса по продуктам: через user api - ошибка; manager-service - ОК")
    @Test
    void userApiAndManagerServiceCallProductWS_Test() throws Exception {
        log.info("<<<Вызов сервиса по продуктам: через user api - ошибка; manager-service - ОК>>>");
        /// обращение через user api  ролью ROLE_CUSTOMER
        final MultiValueMap params = new LinkedMultiValueMap() {
            {add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue());}
            {add(OAuth2ParameterNames.CLIENT_ID, userClientId);}
            {add(OAuth2ParameterNames.USERNAME, user2Login);}
            {add(OAuth2ParameterNames.PASSWORD, user2Pass);}
        };
        final String requestGetProducts = fileToString("xml/getProducts.xml");
        Assert.assertThrows(RuntimeException.class, ()->callSecuredResource(tokenUrl, requestGetProducts, productWS_Url,params,webClient));

        /// обращение через manager-service  ролью ROLE_PRODUCT_MASTER
        final MultiValueMap params2 = new LinkedMultiValueMap() {
            {add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());}
            {add(OAuth2ParameterNames.CLIENT_ID, managerServiceClientId);}
            {add(OAuth2ParameterNames.CLIENT_SECRET, managerServiceSecret);}
        };
        String response = callSecuredResource(tokenUrl, requestGetProducts, productWS_Url, params2, webClient);
        Assert.assertTrue(response.contains("getProductsResponse"));
        log.info("<<<________________________________________________________________________>>>");
    }

}
