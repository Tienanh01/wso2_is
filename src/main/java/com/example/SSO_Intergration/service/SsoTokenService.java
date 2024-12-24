package com.example.SSO_Intergration.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.SSO_Intergration.cache.UserCacheEntry;
import com.example.SSO_Intergration.cache.UserCacheService;
import com.example.SSO_Intergration.modal.User;
import com.example.SSO_Intergration.repository.UserRepository;
import com.example.SSO_Intergration.until.Base64Util;
import com.example.SSO_Intergration.until.CookieUtil;
import com.example.SSO_Intergration.until.OAuth2Constants;
import com.example.SSO_Intergration.until.Oauth2Config;
import com.google.gson.Gson;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthClientResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

@Service
public class SsoTokenService {




    private final UserServiceImpl userServiceImpl;
    private final UserRepository userRepository;
    @Value("${consumerKey}")
    private String consumerKeyInstance;

    @Value("${tokenEndpoint}")
    private String tokenEndpointInstance;

    @Value("${authzGrantType}")
    private String authzGrantTypeInstance;

    @Value("${consumerSecret}")
    private String consumerSecretInstance;

    @Value("${callBackUrl}")
    private String callBackUrlInstance;

    @Value("${edoc.use.secure}")
    private static String isUseSecureInstance ;

    @Value("${edoc.server.host}")
    private  String hostInstance ;
    private static String host_;

    private static String consumerKey;
    private static String consumerSecret;
    private static String callBackUrl;
    private static Boolean isUseSecure;

    private final Gson gson = new Gson();
    private static final Logger logger = Logger.getLogger(String.valueOf(Oauth2Config.class));

    public SsoTokenService(UserServiceImpl userServiceImpl, UserRepository userRepository) {
        this.userServiceImpl = userServiceImpl;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void initStaticFields() {
        consumerKey = consumerKeyInstance;
        consumerSecret = consumerSecretInstance;
        callBackUrl = callBackUrlInstance;
        isUseSecure = Boolean.getBoolean(isUseSecureInstance);
        host_ = hostInstance;
    }

    @Autowired
    private UserCacheService userCacheService ;

    public String getTokenSSO(HttpServletRequest request, HttpServletResponse response, String code) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession();
        }
        String token = null;
        try {
            String sessionState = request.getParameter(OAuth2Constants.SESSION_STATE);
            final OAuthClientRequest.TokenRequestBuilder oAuthTokenRequestBuilder =
                    new OAuthClientRequest.TokenRequestBuilder(tokenEndpointInstance);
            System.out.println(sessionState +"  "+consumerKey +" "+consumerSecret + "  "+callBackUrl +"   "+code) ;
            final OAuthClientRequest accessRequest = oAuthTokenRequestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(consumerKey)
                    .setClientSecret(consumerSecret)
                    .setRedirectURI(callBackUrl)
                    .setCode(code)
                    .buildQueryMessage();
            try {
                SSLContext sc = SSLContext.getInstance("SSL");

                HostnameVerifier hv = new HostnameVerifier() {
                    public boolean verify(String urlHostName, SSLSession session) {
                        return true;
                    }
                };
                TrustManager[] trustAllCerts = {new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs,
                                                   String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs,
                                                   String authType) {
                    }
                }};
                sc.init(null, trustAllCerts, new SecureRandom());

                SSLContext.setDefault(sc);
                HttpsURLConnection.setDefaultHostnameVerifier(hv);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //create OAuth client that uses custom http client under the hood
            final OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            final JSONObject requestObject = requestToJson(accessRequest);

            System.out.println(requestObject);
            System.out.println("--------- reate OAuth client that uses custom http client under the hood");
            final OAuthClientResponse oAuthResponse = oAuthClient.accessToken(accessRequest);
            System.out.println("--------- oAuthClient.accessToken");
            final JSONObject responseObject = responseToJson(oAuthResponse);
            final String idToken = oAuthResponse.getParam("id_token");
            session.setAttribute("requestObject", requestObject);
            session.setAttribute("responseObject", responseObject);

            System.out.println(idToken);

            if (idToken != null) {

                DecodedJWT claims = JwtService.getClaims(idToken);
                String tokenIn = claims.getSubject();
                String organization = claims.getClaim("organization").asString();
//                LOGGER.error("organization: " + organization);
                if (tokenIn != null && organization != null) {
//                    LOGGER.error("Get subject and organization success !!!!");
                    long expiredValue = claims.getExpiresAt().getTime();
                    long startValue = claims.getIssuedAt().getTime();
                    long cookiesAgeValue = expiredValue - startValue;
                    int cookiesAge = Math.toIntExact(cookiesAgeValue);
//                    // Query user from database
                    User user = userServiceImpl.findByUsername(tokenIn);

                    UserCacheEntry userCacheEntry = userCacheService.findByUsername(tokenIn);
                    if (user != null ) {
//                        LOGGER.error("Get user and organization success !!!!");
                        token = tokenIn;
//                        // Create sso cookie with username
                        Cookie ssoCookie = CookieUtil.create(OAuth2Constants.TOKEN_SSO, token, isUseSecure, cookiesAge, host_);
                        response.addCookie(ssoCookie);
//                        // update attribute for user
                        user.setLastLoginDate(new Date());
                        user.setLastLoginIP(getClientIp(request));
                        User userLogin = userRepository.findById(userCacheEntry.getUserId()).get();
                        userLogin.setLastLoginDate(new Date());
                        userLogin.setLastLoginIP(getClientIp(request));

                            userRepository.save(userLogin);
                        String userJson = gson.toJson(userCacheEntry);
                        String userEncodeValue = Base64Util.encode(userJson.getBytes(StandardCharsets.UTF_8));
                        Cookie userLoginCookies = CookieUtil.create(OAuth2Constants.USER_LOGIN, userEncodeValue, isUseSecure, cookiesAge, host_);

                        response.addCookie(userLoginCookies);
                        session.setAttribute("authenticated", true);
//                        // Create cookie for idToken
                        Cookie idTokenCookie = CookieUtil.create(OAuth2Constants.SSO_ID_TOKEN, idToken, isUseSecure, cookiesAge, host_);
//                        // Create session state cookie
                        Cookie stateCookie = CookieUtil.create(OAuth2Constants.SSO_SESSION_STATE, sessionState, isUseSecure, cookiesAge, host_);
                        response.addCookie(stateCookie);
                        response.addCookie(idTokenCookie);
//                        LOGGER.error("Cookies user and organization success 3 !!!!");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("bugs "+e);
            response.sendRedirect("/errors");
        }

        return token;
    }



    public static JSONObject requestToJson(final OAuthClientRequest accessRequest) {

        JSONObject obj = new JSONObject();
        obj.append("tokenEndPoint", accessRequest.getLocationUri());
        obj.append("request body", accessRequest.getBody());

        return obj;
    }

    public static JSONObject responseToJson(final OAuthClientResponse oAuthResponse) {

        JSONObject obj = new JSONObject();
        obj.append("status-code", "200");
        obj.append("id_token", oAuthResponse.getParam("id_token"));
        obj.append("access_token", oAuthResponse.getParam("access_token"));
        return obj;

    }

    private static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }





}
