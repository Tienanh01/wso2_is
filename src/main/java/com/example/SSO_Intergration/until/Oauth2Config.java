package com.example.SSO_Intergration.until;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

@Component
public class Oauth2Config {

    @Value("${consumerKey}")
    private String consumerKeyInstance;

    @Value("${authzEndpoint}")
    private String authzEndpointInstance;

    @Value("${authzGrantType}")
    private String authzGrantTypeInstance;

    @Value("${scope}")
    private String scopeInstance;

    @Value("${callBackUrl}")
    private String callBackUrlInstance;

    private static String consumerKey;
    private static String authzEndpoint;
    private static String authzGrantType;
    private static String scope;
    private static String callBackUrl;

    private static final Logger logger = Logger.getLogger(String.valueOf(Oauth2Config.class));

    @PostConstruct
    private void initStaticFields() {
        consumerKey = consumerKeyInstance;
        authzEndpoint = authzEndpointInstance;
        authzGrantType = authzGrantTypeInstance;
        scope = scopeInstance;
        callBackUrl = callBackUrlInstance;
    }

    public static OAuthClientRequest buildOauthClientRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession(true);
        }
        // Lưu thông tin OAuth vào session
        session.setAttribute(OAuth2Constants.OAUTH2_GRANT_TYPE, authzGrantType);
        session.setAttribute(OAuth2Constants.CONSUMER_KEY, consumerKey);
        session.setAttribute(OAuth2Constants.SCOPE, scope);
        session.setAttribute(OAuth2Constants.CALL_BACK_URL, callBackUrl);
        session.setAttribute(OAuth2Constants.OAUTH2_AUTHZ_ENDPOINT, authzEndpoint);

        OAuthClientRequest.AuthenticationRequestBuilder oAuthAuthenticationRequestBuilder =
                new OAuthClientRequest.AuthenticationRequestBuilder(authzEndpoint);

        oAuthAuthenticationRequestBuilder
                .setClientId(consumerKey)
                .setRedirectURI(callBackUrl)
                .setScope(scope)
                .setResponseType(authzGrantType);

        OAuthClientRequest authzRequest = null;
        try {
            authzRequest = oAuthAuthenticationRequestBuilder.buildQueryMessage();
        } catch (OAuthSystemException e) {

        }
        return authzRequest;
    }
}
