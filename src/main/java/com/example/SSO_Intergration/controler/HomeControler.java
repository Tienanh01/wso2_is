package com.example.SSO_Intergration.controler;

import com.example.SSO_Intergration.cache.UserCacheEntry;
import com.example.SSO_Intergration.until.CookieUtil;
import com.example.SSO_Intergration.until.OAuth2Constants;
import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
public class HomeControler {

    @GetMapping({"/","/home"})
    public String home(HttpServletRequest request, Model model) {

        String userLoginCookie = CookieUtil.getValue(request, OAuth2Constants.USER_LOGIN);
        if (userLoginCookie != null) {
            // Giải mã giá trị cookie
            String userLogin = new String(Base64.getDecoder().decode(userLoginCookie), StandardCharsets.UTF_8);

            // Chuyển giá trị cookie thành đối tượng UserCacheEntry
            UserCacheEntry user = new Gson().fromJson(userLogin, UserCacheEntry.class);



            // Thêm dữ liệu vào model để gửi đến Thymeleaf
            model.addAttribute("user", user);

        }
        return "home";
    }
    @GetMapping(value = "/errors")
    public String Error(HttpServletRequest request, Model model) {
        return "errors";
    }
}
