package ru.d13.projs.webstore.security.token;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class KeycloakRoleConvertor implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        return Stream
                .concat(
                        getRolesFromRealm(jwt)
                        , getRolesFromResource(jwt))
                .collect(Collectors.toSet());

    }

    /**
     * Берем роли из realm_access
     *
     * @param jwt
     * @return
     */
    private Stream<SimpleGrantedAuthority> getRolesFromRealm(Jwt jwt) {
        Map<String, List<String>> realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");
        return Optional
                .ofNullable(realmAccess.get("roles"))
                .orElse(new ArrayList<>())
                .stream()
                .map(role -> decorateRole(role))
                .map(SimpleGrantedAuthority::new);
    }

    private String decorateRole(String role) {
        return String.format("%s%s", ((role.contains("ROLE_")) ? "" : "ROLE_"), role);
    }

    /**
     * Берем роли из resource_access
     *
     * @param jwt
     * @return
     */
    private Stream<SimpleGrantedAuthority> getRolesFromResource(Jwt jwt) {
        try {
            Map<String, JSONObject> resourceAccess = (Map) jwt.getClaims().get("resource_access");
            for (Map.Entry<String, JSONObject> e : resourceAccess.entrySet()) {
                for (Object o : e.getValue().entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    if (entry.getKey().toString().equals("roles")) {
                        return ((List<String>) ((Map.Entry) o).getValue())
                                .stream()
                                .map(role -> decorateRole(role))
                                .map(SimpleGrantedAuthority::new);
                    }
                }
            }
        } catch (Exception e) {
        }
        return Stream.empty();
    }

}