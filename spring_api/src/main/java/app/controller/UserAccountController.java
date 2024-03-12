package app.controller;

import app.util.Util;
import app.model.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@Transactional
@ResponseBody
@RequestMapping("/api/userAccounts")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class UserAccountController {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpServletRequest request;

    @JsonView(Views.UserAccountIndex.class)
    @RequestMapping(value="", method=RequestMethod.GET)
    public ResponseEntity<?> Index(Model model) {
        if (Util.isInvalidSearch(Arrays.asList(new String[] { "id", "name", "email", "active" }), request.getParameter("sc"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int size = request.getParameter("size") != null ? Integer.parseInt(request.getParameter("size")) : 10;
        String sort = request.getParameter("sort") != null ? request.getParameter("sort") : "id";
        String sortDirection = request.getParameter("sort") != null ? (request.getParameter("desc") != null ? "desc" : "asc") : "asc";
        String operator = Util.getOperator(request.getParameter("so"));
        Object search = null;
        if (request.getParameter("sw") != null) {
            Map<String, Class<?>> types = new HashMap<String, Class<?>>();
            types.put("id", Integer.class);
            types.put("active", Boolean.class);
            search = Util.getParameterValue(types, request.getParameter("sw"), request.getParameter("sc"), operator);
        }
        String where = (search != null ? String.format("WHERE u.%s %s ?1", request.getParameter("sc"), operator) : "");
        Query countQuery = em.createQuery(String.format("SELECT COUNT(*) FROM UserAccount u %s", where));
        TypedQuery<UserAccount> selectQuery = em.createQuery(String.format("SELECT u FROM UserAccount u %s ORDER BY %s %s", where, sort, sortDirection), UserAccount.class);
        if (search != null) {
            countQuery.setParameter(1, search);
            selectQuery.setParameter(1, search);
        }
        int count = Integer.parseInt(countQuery.getSingleResult().toString());
        int last = (int)Math.ceil(count / (double)size);
        selectQuery.setFirstResult((page - 1) * size); 
        selectQuery.setMaxResults(size);
        List<UserAccount> userAccounts = selectQuery.getResultList();
        model.addAttribute("userAccounts", userAccounts);
        model.addAttribute("last", last);
        return ResponseEntity.ok(model);
    }

    @JsonView(Views.UserAccountDetail.class)
    @RequestMapping("/{id}")
    public ResponseEntity<?> Detail(@PathVariable Integer id, Model model) {
        UserAccount userAccount = em.find(UserAccount.class, id);
        List<UserRole> userAccountUserRoles = em.createQuery("SELECT userRole FROM UserAccount userAccount join userAccount.userRoles userRole WHERE userAccount.id = :id", UserRole.class).setParameter("id", id).getResultList();
        model.addAttribute("userAccountUserRoles", userAccountUserRoles);
        model.addAttribute("userAccount", userAccount);
        return ResponseEntity.ok(model);
    }

    @JsonView(Views.UserAccountCreate.class)
    @RequestMapping("/create")
    public ResponseEntity<?> Create(Model model) {
        List<Role> roles = em.createQuery("SELECT r FROM Role r", Role.class).getResultList();
        model.addAttribute("roles", roles);
        return ResponseEntity.ok(model);
    }

    @RequestMapping(value="", method=RequestMethod.POST)
    public ResponseEntity<?> Create(@Valid @ModelAttribute("userAccount") UserAccount item, BindingResult result, Model model) throws Exception { 
        if (result.hasErrors()) {
            return Util.getErrors(result.getFieldErrors());
        }
        if (item.getActive() == null) {
            item.setActive(false);
        }
        String token = UUID.randomUUID().toString();
        item.setPasswordResetToken(token);
        item.setPassword(passwordEncoder.encode(UUID.randomUUID().toString().substring(0, 10)));
        em.persist(item);
        String[] roles = request.getParameter("roleId").isEmpty() ? null : request.getParameter("roleId").split(",");
        if (roles != null) {
            for (String role:roles) {
                UserRole userRole = new UserRole();
                UserRolePK userRolePK = new UserRolePK();
                userRolePK.setUserId(item.getId());
                userRolePK.setRoleId(Integer.valueOf(role));
                userRole.setId(userRolePK);
                em.persist(userRole);
            }
        }
        Util.sentMail("welcome", item.getEmail(), token, item.getName());
        return ResponseEntity.ok().build();
    }

    @JsonView(Views.UserAccountEdit.class)
    @RequestMapping("/{id}/edit")
    public ResponseEntity<?> Edit(@PathVariable Integer id, Model model) {
        UserAccount userAccount = em.find(UserAccount.class, id);
        List<UserRole> userAccountUserRoles = em.createQuery("SELECT userRole FROM UserAccount userAccount join userAccount.userRoles userRole WHERE userAccount.id = :id", UserRole.class).setParameter("id", id).getResultList();
        List<Role> roles = em.createQuery("SELECT r FROM Role r", Role.class).getResultList();
        model.addAttribute("userAccountUserRoles", userAccountUserRoles);
        model.addAttribute("roles", roles);
        model.addAttribute("userAccount", userAccount);
        return ResponseEntity.ok(model);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ResponseEntity<?> Update(@ModelAttribute("userAccount") UserAccount userAccount, BindingResult result, Model model) { 
        if (result.hasErrors()) {
            return Util.getErrors(result.getFieldErrors());
        }
        UserAccount item = em.find(UserAccount.class, userAccount.getId());
        item.setName(userAccount.getName());
        item.setEmail(userAccount.getEmail());
        if (userAccount.getPassword() != null && !userAccount.getPassword().isEmpty()) {
            item.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        }
        item.setActive(userAccount.getActive());
        if (item.getActive() == null) {
            item.setActive(false);
        }
        em.persist(item);
        em.createQuery("DELETE FROM UserRole userRole WHERE userRole.id.userId = :userId").setParameter("userId", userAccount.getId()).executeUpdate();
        String[] roles = request.getParameter("roleId").isEmpty() ? null : request.getParameter("roleId").split(",");
        if (roles != null) {
            for (String role:roles) {
                UserRole userRole = new UserRole();
                UserRolePK userRolePK = new UserRolePK();
                userRolePK.setUserId(userAccount.getId());
                userRolePK.setRoleId(Integer.valueOf(role));
                userRole.setId(userRolePK);
                em.persist(userRole);
            }
        }
        return ResponseEntity.ok().build();
    }

    @JsonView(Views.UserAccountDelete.class)
    @RequestMapping("/{id}/delete")
    public ResponseEntity<?> Delete(@PathVariable Integer id, Model model) {
        UserAccount userAccount = em.find(UserAccount.class, id);
        List<UserRole> userAccountUserRoles = em.createQuery("SELECT userRole FROM UserAccount userAccount join userAccount.userRoles userRole WHERE userAccount.id = :id", UserRole.class).setParameter("id", id).getResultList();
        model.addAttribute("userAccountUserRoles", userAccountUserRoles);
        model.addAttribute("userAccount", userAccount);
        return ResponseEntity.ok(model);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<?> DeleteConfirm(@PathVariable Integer id) {
        UserAccount item = em.find(UserAccount.class, id);
        em.remove(item);
        return ResponseEntity.ok().build();
    }  
}