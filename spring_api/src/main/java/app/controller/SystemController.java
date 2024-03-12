package app.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonView;
import app.model.Views;
import app.model.UserAccount;
import app.util.Util;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Controller
@Transactional
public class SystemController {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @JsonView(Views.UserAccountEdit.class)
    @RequestMapping("/api/profile")
    public ResponseEntity<?> Profile(Model model) {
        UserAccount userAccount = em.find(UserAccount.class, Util.getUser(em).getId());
        model.addAttribute("userAccount", userAccount);
        return ResponseEntity.ok(model);
    }
    
    @RequestMapping(value="/api/updateProfile", method=RequestMethod.POST)
    public ResponseEntity<?> UpdateProfile(@RequestBody UserAccount userAccount) { 
        UserAccount item = em.find(UserAccount.class, Util.getUser(em).getId());
        item.setName(userAccount.getName());
        item.setEmail(userAccount.getEmail());
        if (userAccount.getPassword() != null && !userAccount.getPassword().isEmpty()) {
            item.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        }
        em.persist(item);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @RequestMapping("/api/stack")
    public String Stack() {
        return "Angular 16 + Spring API 6 + MySQL";
    }
}