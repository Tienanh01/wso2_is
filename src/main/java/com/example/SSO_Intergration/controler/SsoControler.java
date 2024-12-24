package com.example.SSO_Intergration.controler;

import com.example.SSO_Intergration.service.SsoTokenService;
import com.example.SSO_Intergration.until.CookieUtil;
import com.example.SSO_Intergration.until.OAuth2Constants;
import com.example.SSO_Intergration.until.Oauth2Config;
import com.example.SSO_Intergration.until.PropsUtil;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class SsoControler {

    @Autowired
    private SsoTokenService ssoTokenService;

    @Value("edoc.server.host")
    private String hostInstance;

    private static String host_;

    @PostConstruct
    private void initStaticFields() {
        host_ = hostInstance;
    }

    @GetMapping(value = "/checkLogin")
    public void callback (HttpServletRequest request , HttpServletResponse response) throws IOException {

        String code = request.getParameter(OAuth2Constants.CODE);
        String session_state = request.getParameter(OAuth2Constants.SESSION_STATE);
        String redirect_url = request.getParameter("return_url");
        if (code == null || session_state == null) {
            OAuthClientRequest authRequest = Oauth2Config.buildOauthClientRequest(request);
            System.out.println(authRequest.getLocationUri());
            response.sendRedirect(authRequest.getLocationUri());
            return;
        }

        String tokenSSO = ssoTokenService.getTokenSSO(request, response, code);
        if (tokenSSO == null) {
            response.sendRedirect("/errors");
        }else {
            System.out.println(redirect_url);
            response.sendRedirect(redirect_url);
            System.out.println(redirect_url);
        }
    }

    @GetMapping(value = "/logoutSSO")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("call logout ");
        HttpSession session = request.getSession(false);
        String session_state = CookieUtil.getValue(request, OAuth2Constants.SESSION_STATE);

        String idToken = CookieUtil.getValue(request, OAuth2Constants.SSO_ID_TOKEN);
        String redirectUri = PropsUtil.get("callBackUrl");;
        if (session_state != null && idToken != null) {
            StringBuilder logoutUrl = new StringBuilder();
            String OIDC_LOGOUT = PropsUtil.get("OIDC_LOGOUT_ENDPOINT");
            logoutUrl.append(OIDC_LOGOUT);
            logoutUrl.append("?id_token_hint=");
            logoutUrl.append(idToken);
            logoutUrl.append("&post_logout_redirect_uri=");
            logoutUrl.append(redirectUri);
            logoutUrl.append("&state=");
            logoutUrl.append(session_state);

            // clear cookies
            CookieUtil.clear(response, OAuth2Constants.SSO_ID_TOKEN, host_);
            CookieUtil.clear(response, OAuth2Constants.SESSION_STATE, host_);
            CookieUtil.clear(response, OAuth2Constants.TOKEN_SSO, host_);
            CookieUtil.clear(response, OAuth2Constants.USER_LOGIN, host_);

            // clear session
            if (session != null) {
                session.invalidate();
            }
            System.out.println(logoutUrl.toString());
            response.sendRedirect(logoutUrl.toString());
        }
    }
}
