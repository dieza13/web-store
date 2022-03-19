package ru.d13.projs.webstore.configs;

import com.sun.xml.ws.transport.http.servlet.SpringBinding;
import com.sun.xml.ws.transport.http.servlet.WSSpringServlet;
import lombok.SneakyThrows;
import org.jvnet.jax_ws_commons.spring.SpringService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import ru.d13.projs.webstore.endpoints.CustomerWS;
import ru.d13.projs.webstore.endpoints.OrderWS;
import ru.d13.projs.webstore.endpoints.ProductWS;

import javax.jws.WebService;

@Configuration
@PropertySource("service-endpoints.properties")
public class WebServiceConfig extends WsConfigurerAdapter {

    private static String ERR_BAD_SERVICE = "Нельзя создать веб-сервис, класс %s не содержит аннотацию WebService";

    @Bean
    public SpringBinding customerWSBinding(
            @Value("${service.customer.endpoint}") String pattern
            ,CustomerWS customerWS
    ) {

        return binding(pattern,customerWS);
    }

    @Bean
    public SpringBinding orderWSBinding(
            @Value("${service.order.endpoint}") String pattern
            , OrderWS orderWS
            ) {
        return binding(pattern, orderWS);
    }

    @Bean
    public SpringBinding productWSBinding(
            @Value("${service.product.endpoint}") String pattern
            , ProductWS productWS) {
        return binding(pattern, productWS);
    }


    @SneakyThrows
    private SpringBinding binding(String urlPattern, Object service) {
        if (!service.getClass().isAnnotationPresent(WebService.class)) {
            throw new RuntimeException(String.format(ERR_BAD_SERVICE,WebService.class.getName()));
        }
        SpringService springService = new SpringService();
        springService.setBean(service);
        SpringBinding binding = new SpringBinding();
        binding.setService(springService.getObject());
        binding.setUrl(urlPattern);

        return binding;
    }
//
    @Bean
    public ServletRegistrationBean<WSSpringServlet> customerServlet(
            @Value("${service.customer.endpoint}") String pattern
            ,@Value("${service.customer.name}") String servletName) {
        return createServlet(servletName,pattern);
    }

    @Bean
    public ServletRegistrationBean<WSSpringServlet> orderServlet(
            @Value("${service.order.endpoint}") String pattern
            ,@Value("${service.order.name}") String servletName) {
        return createServlet(servletName,pattern);
    }

    @Bean
    public ServletRegistrationBean<WSSpringServlet> productServlet(
            @Value("${service.product.endpoint}") String pattern
            ,@Value("${service.product.name}") String servletName) {
        return createServlet(servletName,pattern);
    }

    private ServletRegistrationBean<WSSpringServlet> createServlet(String servletName, String urlPattern) {
        WSSpringServlet wss = new WSSpringServlet();
        ServletRegistrationBean<WSSpringServlet> serv =  new ServletRegistrationBean<>(wss, urlPattern);
        serv.setLoadOnStartup(1);
        serv.setName(servletName);
        return serv;
    }
}
