package app.controller;

import app.util.Util;
import app.model.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
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

@Controller
@Transactional
@ResponseBody
@RequestMapping("/api/brands")
@PreAuthorize("hasAnyAuthority('ADMIN','USER')")
public class BrandController {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpServletRequest request;

    @JsonView(Views.BrandIndex.class)
    @RequestMapping(value="", method=RequestMethod.GET)
    public ResponseEntity<?> Index(Model model) {
        if (Util.isInvalidSearch(Arrays.asList(new String[] { "id", "name" }), request.getParameter("sc"))) {
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
            search = Util.getParameterValue(types, request.getParameter("sw"), request.getParameter("sc"), operator);
        }
        String where = (search != null ? String.format("WHERE b.%s %s ?1", request.getParameter("sc"), operator) : "");
        Query countQuery = em.createQuery(String.format("SELECT COUNT(*) FROM Brand b %s", where));
        TypedQuery<Brand> selectQuery = em.createQuery(String.format("SELECT b FROM Brand b %s ORDER BY %s %s", where, sort, sortDirection), Brand.class);
        if (search != null) {
            countQuery.setParameter(1, search);
            selectQuery.setParameter(1, search);
        }
        int count = Integer.parseInt(countQuery.getSingleResult().toString());
        int last = (int)Math.ceil(count / (double)size);
        selectQuery.setFirstResult((page - 1) * size); 
        selectQuery.setMaxResults(size);
        List<Brand> brands = selectQuery.getResultList();
        model.addAttribute("brands", brands);
        model.addAttribute("last", last);
        return ResponseEntity.ok(model);
    }

    @JsonView(Views.BrandDetail.class)
    @RequestMapping("/{id}")
    public ResponseEntity<?> Detail(@PathVariable Integer id, Model model) {
        Brand brand = em.find(Brand.class, id);
        List<Product> brandProducts = em.createQuery("SELECT product FROM Brand brand join brand.products product WHERE brand.id = :id", Product.class).setParameter("id", id).getResultList();
        model.addAttribute("brandProducts", brandProducts);
        model.addAttribute("brand", brand);
        return ResponseEntity.ok(model);
    }

    @JsonView(Views.BrandCreate.class)
    @RequestMapping("/create")
    public ResponseEntity<?> Create(Model model) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value="", method=RequestMethod.POST)
    public ResponseEntity<?> Create(@Valid @RequestBody Brand item, BindingResult result, Model model) { 
        if (result.hasErrors()) {
            return Util.getErrors(result.getFieldErrors());
        }
        em.persist(item);
        return ResponseEntity.ok().build();
    }

    @JsonView(Views.BrandEdit.class)
    @RequestMapping("/{id}/edit")
    public ResponseEntity<?> Edit(@PathVariable Integer id, Model model) {
        Brand brand = em.find(Brand.class, id);
        model.addAttribute("brand", brand);
        return ResponseEntity.ok(model);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ResponseEntity<?> Update(@RequestBody Brand brand, BindingResult result, Model model) { 
        if (result.hasErrors()) {
            return Util.getErrors(result.getFieldErrors());
        }
        Brand item = em.find(Brand.class, brand.getId());
        item.setName(brand.getName());
        em.persist(item);
        return ResponseEntity.ok().build();
    }

    @JsonView(Views.BrandDelete.class)
    @RequestMapping("/{id}/delete")
    public ResponseEntity<?> Delete(@PathVariable Integer id, Model model) {
        Brand brand = em.find(Brand.class, id);
        model.addAttribute("brand", brand);
        return ResponseEntity.ok(model);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<?> DeleteConfirm(@PathVariable Integer id) {
        Brand item = em.find(Brand.class, id);
        em.remove(item);
        return ResponseEntity.ok().build();
    }  
}