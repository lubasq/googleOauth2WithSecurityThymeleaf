package com.pai.pai11.security;

import com.pai.pai11.entity.User;
import com.pai.pai11.dao.userDao;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

@Component
public class GoogleAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
 
    @Autowired
    private userDao dao;
    
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
 
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
        
        registerIfNotExist((OAuth2Authentication) authentication);
        handle(request, response, authentication);
    }
    
    public void registerIfNotExist(OAuth2Authentication authentication){
        LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) authentication.getUserAuthentication().getDetails();
        
        User user = new User();        
        user.setName((String) properties.get("email"));        
        user.setLogin((String) properties.get("name"));
        user.setSurname(" ");
        dao.save(user);
    }
 
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
        String targetUrl = determineTargetUrl(authentication);
 
        if (response.isCommitted()) {
            return;
        }
 
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
 
    protected String determineTargetUrl(Authentication authentication) {
        
        return "/profile";
    }
 
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }
    
}
