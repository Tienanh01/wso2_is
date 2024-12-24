package com.example.SSO_Intergration.auth;

import com.example.SSO_Intergration.until.CookieUtil;
import com.example.SSO_Intergration.until.OAuth2Constants;
import com.example.SSO_Intergration.until.Oauth2Config;
import com.example.SSO_Intergration.until.PropsUtil;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor  implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        String userLogin = CookieUtil.getValue(httpServletRequest, OAuth2Constants.TOKEN_SSO);
        String host = PropsUtil.get("edoc.server.host");
     String userLoginInfo = CookieUtil.getValue(httpServletRequest, OAuth2Constants.USER_LOGIN);
        System.out.println( "user login   " +userLogin );
        System.out.println( "user login  info " +userLoginInfo  );

        if (userLogin == null && userLoginInfo == null) {
            try {
                System.out.println(" -------------------------------- VA 2 ");
                OAuthClientRequest authClientRequest = Oauth2Config.buildOauthClientRequest(httpServletRequest);
                httpServletResponse.sendRedirect(authClientRequest.getLocationUri());
                return false;
            } catch (Exception e) {
                // Log lỗi nếu cần
                System.out.println(" -------------------------------- VA 3 ");
                return false;
            }


        }
        if(userLogin == null) {
            CookieUtil.clear(httpServletResponse, OAuth2Constants.TOKEN_SSO, host);
//            OAuthClientRequest authClientRequest = Oauth2Config.buildOauthClientRequest(httpServletRequest);
//            httpServletResponse.sendRedirect(authClientRequest.getLocationUri());
            if (userLoginInfo != null ) {
                CookieUtil.clear(httpServletResponse, OAuth2Constants.USER_LOGIN, host);
               return true ;
            }


        }


        return true;





    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        /*logger.info(" Post handler Request URL::" + httpServletRequest.getRequestURL().toString()
                + " Sent to Handler :: Current Time=" + System.currentTimeMillis());*/

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
       /* long startTime = (Long) httpServletRequest.getAttribute("startTime");
        logger.info("After completion Request URL::" + httpServletRequest.getRequestURL().toString()
                + ":: End Time=" + System.currentTimeMillis());
        logger.info("After completion Request URL::" + httpServletRequest.getRequestURL().toString()
                + ":: Time Taken=" + (System.currentTimeMillis() - startTime));*/
    }


}
