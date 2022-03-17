package ru.d13.projs.webstore.clients;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
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

import static ru.d13.projs.webstore.utils.TestUtil.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:app-props.properties")
public class ClientCallsTest {

    @Value(value = "${token-url}")
    private String tokenUrl;

    @Value(value = "${manager-service.clientId}")
    private String managerServiceClientId;
    @Value(value = "${manager-service.secret}")
    private String managerServiceSecret;
    /////
    @Value(value = "${user.clientId}")
    private String userClientId;
    //
    @Value(value = "${user1.login}")
    private String user1Login;
    @Value(value = "${user1.pass}")
    private String user1Pass;
    //
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

    @BeforeAll
    public static void init() {
        webClient = WebClient.builder().build();
    }

    @SneakyThrows
    @DisplayName("Вызов сервиса по клиентам сервисом manager-service c авторизацией по clientId/secret")
    @Test
    void managerServiceCallCustomerWS_Test() throws Exception {
        MultiValueMap params = new LinkedMultiValueMap() {
            {add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());}
            {add(OAuth2ParameterNames.CLIENT_ID, managerServiceClientId);}
            {add(OAuth2ParameterNames.CLIENT_SECRET, managerServiceSecret);}
        };

        String requestGetCustomers = fileToString("xml/getCustomers.xml");
        String response = callSecuredResource(tokenUrl, requestGetCustomers, customerWS_Url, params, webClient);
        Assert.assertTrue(response.contains("getCustomersResponse"));
    }

    @SneakyThrows
    @DisplayName("Ошибка при вызове сервиса по клиентам сервисом manager-service параметров аутентификации")
    @Test
    void managerServiceCallCustomerWSDirectlyAuthError_Test() throws Exception {

        String requestGetCustomers = fileToString("xml/getCustomers.xml");
        Assert.assertThrows(RuntimeException.class,()->callResource(requestGetCustomers, customerWS_Url, webClient));
    }

    @SneakyThrows
    @DisplayName("Вызов сервиса по заказам через user api c аутентификацией по login/password, но для пользователя без роли ROLE_CUSTOMER")
    @Test
    void userApiServiceCallOrderWSWithLoginPassWithoutRole_Test() throws Exception {
//        /// обращение первого клиента
        MultiValueMap params = new LinkedMultiValueMap() {
            {add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue());}
            {add(OAuth2ParameterNames.CLIENT_ID, userClientId);}
            {add(OAuth2ParameterNames.USERNAME, user1Login);}
            {add(OAuth2ParameterNames.PASSWORD, user1Pass);}
        };

        String requestGetCustomers = fileToString("xml/getOrderByCode_111.xml");
        Assert.assertThrows(RuntimeException.class,()->callSecuredResource(tokenUrl, requestGetCustomers, orderWS_Url,params,webClient));
    }
    @SneakyThrows
    @DisplayName("Вызов сервиса по заказам через user api c аутентификацией по login/password и авторизацией по роли ROLE_CUSTOMER")
    @Test
    void userApiServiceCallOrderWSWithLoginPassWithRole_Test() throws Exception {//

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
    }

    @SneakyThrows
    @DisplayName("Ошибка при вызове сервиса по заказам через user api c неправильным паролем")
    @Test
    void userApiServiceCallOrderWSWithBadPass_Test() throws Exception {
        /// обращение первого клиента
        MultiValueMap params = new LinkedMultiValueMap() {
            {add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue());}
            {add(OAuth2ParameterNames.CLIENT_ID, userClientId);}
            {add(OAuth2ParameterNames.USERNAME, user1Login);}
            {add(OAuth2ParameterNames.PASSWORD, "неправильный пароль");}
        };

        String requestGetCustomers = fileToString("xml/getOrderByCode_111.xml");
        Assert.assertThrows(RuntimeException.class, ()->callSecuredResource(tokenUrl, requestGetCustomers, orderWS_Url,params,webClient));
    }

    @SneakyThrows
    @DisplayName("Вызов сервиса по продуктам: через user api - ошибка; manager-service - ОК")
    @Test
    void userApiAndManagerServiceCallProductWS_Test() throws Exception {
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
    }

}
