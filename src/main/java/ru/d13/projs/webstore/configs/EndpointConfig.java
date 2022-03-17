package ru.d13.projs.webstore.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:service-endpoints.properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointConfig {
    @Value("${service.customer.endpoint}")
    String CUSTOMER_PATTERN;
    @Value("${service.customer.name}")
    String CUSTOMER_SERVLET_NAME;
    @Value("${service.order.endpoint}")
    String ORDER_PATTERN;
    @Value("${service.order.name}")
    String ORDER_SERVLET_NAME;
    @Value("${service.product.endpoint}")
    String PRODUCT_PATTERN;
    @Value("${service.product.name}")
    String PRODUCT_SERVLET_NAME;
}
