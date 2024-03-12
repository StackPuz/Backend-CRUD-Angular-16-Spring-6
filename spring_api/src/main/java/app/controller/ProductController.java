package app.controller;

import app.util.Util;
import app.model.*;
import java.math.BigDecimal;
import java.util.Date;
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
import org.springframework.web.multipart.MultipartFile;
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
@RequestMapping("/api/products")
@PreAuthorize("hasAnyAuthority('ADMIN','USER')")
public class ProductController {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpServletRequest request;

    @JsonView(Views.ProductIndex.class)
    @RequestMapping(value="", method=RequestMethod.GET)
    public ResponseEntity<?> Index(Model model) {
        if (Util.isInvalidSearch(Arrays.asList(new String[] { "id", "image", "name", "price", "brand.name", "userAccount.name" }), request.getParameter("sc"))) {
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
            types.put("price", BigDecimal.class);
            search = Util.getParameterValue(types, request.getParameter("sw"), request.getParameter("sc"), operator);
        }
        String where = (search != null ? String.format("WHERE p.%s %s ?1", request.getParameter("sc"), operator) : "");
        Query countQuery = em.createQuery(String.format("SELECT COUNT(*) FROM Product p %s", where));
        TypedQuery<Product> selectQuery = em.createQuery(String.format("SELECT p FROM Product p %s ORDER BY %s %s", where, sort, sortDirection), Product.class);
        if (search != null) {
            countQuery.setParameter(1, search);
            selectQuery.setParameter(1, search);
        }
        int count = Integer.parseInt(countQuery.getSingleResult().toString());
        int last = (int)Math.ceil(count / (double)size);
        selectQuery.setFirstResult((page - 1) * size); 
        selectQuery.setMaxResults(size);
        List<Product> products = selectQuery.getResultList();
        model.addAttribute("products", products);
        model.addAttribute("last", last);
        return ResponseEntity.ok(model);
    }

    @JsonView(Views.ProductDetail.class)
    @RequestMapping("/{id}")
    public ResponseEntity<?> Detail(@PathVariable Integer id, Model model) {
        Product product = em.find(Product.class, id);
        model.addAttribute("product", product);
        return ResponseEntity.ok(model);
    }

    @JsonView(Views.ProductCreate.class)
    @RequestMapping("/create")
    public ResponseEntity<?> Create(Model model) {
        List<Brand> brands = em.createQuery("SELECT b FROM Brand b", Brand.class).getResultList();
        model.addAttribute("brands", brands);
        return ResponseEntity.ok(model);
    }

    @RequestMapping(value="", method=RequestMethod.POST)
    public ResponseEntity<?> Create(@Valid @ModelAttribute("product") Product item, BindingResult result, Model model, @RequestParam MultipartFile imageFile) throws Exception { 
        if (result.hasErrors()) {
            return Util.getErrors(result.getFieldErrors());
        }
        item.setImage(Util.getFile("products", imageFile));
        item.setUserAccount(Util.getUser(em));
        item.setCreateDate(new Date());
        em.persist(item);
        return ResponseEntity.ok().build();
    }

    @JsonView(Views.ProductEdit.class)
    @RequestMapping("/{id}/edit")
    public ResponseEntity<?> Edit(@PathVariable Integer id, Model model) {
        Product product = em.find(Product.class, id);
        List<Brand> brands = em.createQuery("SELECT b FROM Brand b", Brand.class).getResultList();
        model.addAttribute("brands", brands);
        model.addAttribute("product", product);
        return ResponseEntity.ok(model);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ResponseEntity<?> Update(@ModelAttribute("product") Product product, BindingResult result, Model model, @RequestParam MultipartFile imageFile) throws Exception { 
        if (result.hasErrors()) {
            return Util.getErrors(result.getFieldErrors());
        }
        Product item = em.find(Product.class, product.getId());
        item.setName(product.getName());
        item.setPrice(product.getPrice());
        item.setBrand(product.getBrand());
        String image = Util.getFile("products", imageFile);
        item.setImage(image != null ? image : item.getImage());
        em.persist(item);
        return ResponseEntity.ok().build();
    }

    @JsonView(Views.ProductDelete.class)
    @RequestMapping("/{id}/delete")
    public ResponseEntity<?> Delete(@PathVariable Integer id, Model model) {
        Product product = em.find(Product.class, id);
        model.addAttribute("product", product);
        return ResponseEntity.ok(model);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<?> DeleteConfirm(@PathVariable Integer id) {
        Product item = em.find(Product.class, id);
        em.remove(item);
        return ResponseEntity.ok().build();
    }  
}