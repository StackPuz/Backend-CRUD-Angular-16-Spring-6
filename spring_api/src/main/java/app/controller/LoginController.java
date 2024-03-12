package app.controller;

import app.util.Util;
import app.model.UserAccount;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@CrossOrigin("*")
@RequestMapping("/api")
@Controller
@Transactional
public class LoginController {

    @PersistenceContext
    private EntityManager em;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private String jwtSecret;
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public ResponseEntity<?> Login(@RequestBody UserAccount login) throws Exception {
        String userName = login.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        if (!passwordEncoder.matches(login.getPassword(), userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials");
        }
        if (!userDetails.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is disabled");
        }
        UsernamePasswordAuthenticationToken authenToken = new UsernamePasswordAuthenticationToken(userName, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenToken);
        String token = Jwts.builder()
            .setSubject(userName)
            .setExpiration(new Date(System.currentTimeMillis() + (60 * 60 * 24 * 1000)))
            .signWith(SignatureAlgorithm.HS256, jwtSecret).compact();
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("token", token);
        response.put("user", getUser(userName));
        return ResponseEntity.ok(response);
    }

    @RequestMapping("/user")
    public ResponseEntity<?> User(Principal principal) {
        return ResponseEntity.ok(getUser(principal.getName()));
    }
    
    public Map<String, Object> getUser(String name) {
        Map<String, Object> user = new HashMap<String, Object>();
        user.put("name", name);
        user.put("menu", Util.getMenu());
        return user;
    }
    
    @RequestMapping("/logout")
    public ResponseEntity<?> Logout() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value="resetPassword", method=RequestMethod.POST)
    public ResponseEntity<?> ResetPasswordPost(@RequestBody UserAccount login, Model model) throws Exception
    {
        String email = login.getEmail();
        UserAccount user = getUser("email", email);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            em.persist(user);
            Util.sentMail("reset", email, token);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @RequestMapping("changePassword/{token}")
    public ResponseEntity<?> ChangePassword(@PathVariable String token, Model model)
    {
        UserAccount user = getUser("passwordResetToken", token);
        if (user != null) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value="changePassword/{token}", method=RequestMethod.POST)
    public ResponseEntity<?> ChangePasswordPost(@RequestBody UserAccount login, @PathVariable String token, Model model)
    {
        UserAccount user = getUser("passwordResetToken", token);
        if (user != null) {
            String password = login.getPassword();
            user.setPassword(passwordEncoder.encode(password));
            user.setPasswordResetToken(null);
            em.persist(user);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
    
    public UserAccount getUser(String key, String value) {
        List<UserAccount> users = em.createQuery("SELECT u FROM UserAccount u where u. " + key + " = :value" , UserAccount.class).setParameter("value", value).getResultList(); //getSingleResult() maybe throw exception
        return (users.size() > 0 ? users.get(0) : null);
    }
}