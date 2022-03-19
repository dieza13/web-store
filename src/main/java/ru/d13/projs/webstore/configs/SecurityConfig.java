package ru.d13.projs.webstore.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import javax.servlet.ServletContext;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ServletContext servletContext;
    @Autowired
    private EndpointConfig endpointConfig;
    @Autowired
    private Converter<Jwt, Collection<GrantedAuthority>> tokenConverter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String ctx = servletContext.getContextPath();
        http.authorizeRequests()
                .regexMatchers("\\/*\\/*.+\\?wsdl","\\/*\\/*.+\\?xsd*.+").permitAll()

                .mvcMatchers(endpointConfig.getORDER_PATTERN()).hasAnyRole("CUSTOMER","ORDER_MASTER")
                .mvcMatchers(endpointConfig.getCUSTOMER_PATTERN()).hasAnyRole("CUSTOMER")
                .mvcMatchers(endpointConfig.getPRODUCT_PATTERN()).hasAnyRole("PRODUCT_MASTER")

                .anyRequest()
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());

    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(tokenConverter);
        return jwtConverter;
    }

}
