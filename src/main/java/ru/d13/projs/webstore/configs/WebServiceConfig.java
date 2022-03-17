package ru.d13.projs.webstore.configs;

import com.sun.xml.ws.transport.http.servlet.WSServlet;
import com.sun.xml.ws.transport.http.servlet.WSServletContextListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.remoting.jaxws.SimpleJaxWsServiceExporter;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;

import javax.servlet.ServletContext;

@EnableWs
@Configuration
@PropertySource("service-endpoints.properties")
public class WebServiceConfig extends WsConfigurerAdapter {

    private boolean IS_PARAMETERS_SET = false;
    @Autowired
    private EndpointConfig endpointConfig;

    @Bean
    public ServletRegistrationBean<WSServlet> customerServlet(ServletContext servletContext) {
        return createServlet(endpointConfig.getCUSTOMER_SERVLET_NAME(), endpointConfig.getCUSTOMER_PATTERN(), servletContext);
    }

    @Bean
    public ServletRegistrationBean<WSServlet> orderServlet(ServletContext servletContext) {
        return createServlet(endpointConfig.getORDER_SERVLET_NAME(), endpointConfig.getORDER_PATTERN(), servletContext);
    }

    @Bean
    public ServletRegistrationBean<WSServlet> productServlet(ServletContext servletContext) {
        return createServlet(endpointConfig.getPRODUCT_SERVLET_NAME(), endpointConfig.getPRODUCT_PATTERN(), servletContext);
    }

    private ServletRegistrationBean<WSServlet> createServlet(String servletName, String urlPattern, ServletContext servletContext) {
        if (!IS_PARAMETERS_SET) {
            IS_PARAMETERS_SET = !IS_PARAMETERS_SET;
            servletContext.addListener(new WSServletContextListener());
        }
        WSServlet wss = new WSServlet();
        ServletRegistrationBean<WSServlet> serv =  new ServletRegistrationBean<>(wss, urlPattern);
        serv.setLoadOnStartup(1);
        serv.setName(servletName);
        return serv;
    }

    @Bean
    public SimpleJaxWsServiceExporter simpleJaxWsServiceExporter() {
        return new SimpleJaxWsServiceExporter();
    }
}
