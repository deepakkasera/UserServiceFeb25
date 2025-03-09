package com.example.userservicefeb25;

import com.example.userservicefeb25.security.repositories.JpaRegisteredClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.UUID;

@SpringBootTest
class UserServiceFeb25ApplicationTests {
    @Autowired
    private JpaRegisteredClientRepository registeredClientRepository;

//    @Test
//    void contextLoads() {
//    }

    @Test
    public void testAddClientToDB() {
		RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("oidc-client1")
				.clientSecret("$2a$12$utJVovjICW8GgR.sZ/1jkecRShLBljxzCxMN..Efp0BdNBrcgAhhG")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri("https://oauth.pstmn.io/v1/callback")
				.postLogoutRedirectUri("http://127.0.0.1:8080/")
//				.scope(OidcScopes.OPENID)
//				.scope(OidcScopes.PROFILE)
				.scope("ADMIN")
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.build();

        registeredClientRepository.save(oidcClient);
    }

}
