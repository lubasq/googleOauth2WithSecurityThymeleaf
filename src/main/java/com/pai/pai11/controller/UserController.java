package com.pai.pai11.controller;

import com.pai.pai11.dao.userDao;
import com.pai.pai11.entity.User;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.LinkedHashMap;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@Controller
public class UserController {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private userDao dao;
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        //zwrócenie nazwy widoku logowania - login.html
        return "login";
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerPage(Model m) {
        //dodanie do modelu nowego użytkownika
        m.addAttribute("user", new User());
        //zwrócenie nazwy widoku rejestracji - register.html
        return "register";
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerPagePOST(@ModelAttribute(value = "user") User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dao.save(user);
        //przekierowanie do adresu url: /login
        return "redirect:/login";
    }
    
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profilePage(Model m, Principal principal) {
        User c = dao.findByLogin(principal.getName());
        
        //dodanie do modelu obiektu user - aktualnie zalogowanego użytkownika:
        if(null == c){
            System.out.println("Logowanie przez google"); 
            OAuth2Authentication googleUserAuth = (OAuth2Authentication) principal;
            LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) googleUserAuth.getUserAuthentication().getDetails();
            System.out.println("Login: " + properties.get("name"));
            c = dao.findByLogin((String) properties.get("name"));
            m.addAttribute("user", c);
        } else {
            System.out.println("Normalne logowanie");
            m.addAttribute("user", c);
        }
        
        //zwrócenie nazwy widoku profilu użytkownika - profile.html
        return "profile";
    }
    
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String profilePage(Model m) {
        //dodanie do modelu listy wszystkich użytkowników
        m.addAttribute("userlist", dao.findAll());
        //zwrócenie nazwy widoku wyświetlającego wszystkich użytkowników
        return "users";
    }
    
    @Transactional
    @RequestMapping(value = "/deleteUser", method = RequestMethod.GET)
    public String deleteUser(@ModelAttribute(value = "user") String login, Principal principal) {
        dao.deleteByLogin(login);
        if(principal.getName().equals(login)){
            return "redirect:/logout";
        }else{
            return "redirect:/users";
        }
    }
    
    @RequestMapping(value = "/googleUser", method = RequestMethod.GET)
    public String googleProfile(Model m) {   
        return "googleUser";
    }

}
