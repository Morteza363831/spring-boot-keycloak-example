package org.camunda.bpm.getstarted.springbootkeycloakexample.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GrantedAuthoritiesMapperImpl implements GrantedAuthoritiesMapper {

    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String deliveryServiceClientId;

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        authorities.forEach(authority -> {
            if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                // Map the claims found in idToken to one or more GrantedAuthority.
                OidcIdToken idToken = oidcUserAuthority.getIdToken();
                mappedAuthorities.addAll(extractClientRoles(idToken));
            }
        });
        return mappedAuthorities;
    }

    private Collection<GrantedAuthority> extractClientRoles(OidcIdToken idToken) {

        Map<String, Object> resourceAccess = idToken.getClaim(RESOURCE_ACCESS);
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        if (deliveryServiceClientId == null || resourceAccess == null
                || (resource = (Map<String, Object>) resourceAccess.get(deliveryServiceClientId)) == null
                || (resourceRoles = (Collection<String>) resource.get(ROLES)) == null) {
            return Set.of();
        }
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .collect(Collectors.toSet());
    }
}