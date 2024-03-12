package app.controller;

import app.util.Util;
import app.model.*;
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
@RequestMapping("/api/orderHeaders")
@PreAuthorize("hasAnyAuthority('ADMIN','USER')")
public class OrderHeaderController {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpServletRequest request;

    @JsonView(Views.OrderHeaderIndex.class)
    @RequestMapping(value="", method=RequestMethod.GET)
    public ResponseEntity<?> Index(Model model) {
        if (Util.isInvalidSearch(Arrays.asList(new String[] { "id", "customer.name", "orderDate" }), request.getParameter("sc"))) {
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
            types.put("orderDate", Date.class);
            search = Util.getParameterValue(types, request.getParameter("sw"), request.getParameter("sc"), operator);
        }
        String where = (search != null ? String.format("WHERE o.%s %s ?1", request.getParameter("sc"), operator) : "");
        Query countQuery = em.createQuery(String.format("SELECT COUNT(*) FROM OrderHeader o %s", where));
        TypedQuery<OrderHeader> selectQuery = em.createQuery(String.format("SELECT o FROM OrderHeader o %s ORDER BY %s %s", where, sort, sortDirection), OrderHeader.class);
        if (search != null) {
            countQuery.setParameter(1, search);
            selectQuery.setParameter(1, search);
        }
        int count = Integer.parseInt(countQuery.getSingleResult().toString());
        int last = (int)Math.ceil(count / (double)size);
        selectQuery.setFirstResult((page - 1) * size); 
        selectQuery.setMaxResults(size);
        List<OrderHeader> orderHeaders = selectQuery.getResultList();
        model.addAttribute("orderHeaders", orderHeaders);
        model.addAttribute("last", last);
        return ResponseEntity.ok(model);
    }

    @JsonView(Views.OrderHeaderDetail.class)
    @RequestMapping("/{id}")
    public ResponseEntity<?> Detail(@PathVariable Integer id, Model model) {
        OrderHeader orderHeader = em.find(OrderHeader.class, id);
        List<OrderDetail> orderHeaderOrderDetails = em.createQuery("SELECT orderDetail FROM OrderHeader orderHeader join orderHeader.orderDetails orderDetail WHERE orderHeader.id = :id", OrderDetail.class).setParameter("id", id).getResultList();
        model.addAttribute("orderHeaderOrderDetails", orderHeaderOrderDetails);
        model.addAttribute("orderHeader", orderHeader);
        return ResponseEntity.ok(model);
    }

    @JsonView(Views.OrderHeaderCreate.class)
    @RequestMapping("/create")
    public ResponseEntity<?> Create(Model model) {
        List<Customer> customers = em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
        model.addAttribute("customers", customers);
        return ResponseEntity.ok(model);
    }

    @RequestMapping(value="", method=RequestMethod.POST)
    public ResponseEntity<?> Create(@Valid @RequestBody OrderHeader item, BindingResult result, Model model) { 
        if (result.hasErrors()) {
            return Util.getErrors(result.getFieldErrors());
        }
        em.persist(item);
        return ResponseEntity.ok().build();
    }

    @JsonView(Views.OrderHeaderEdit.class)
    @RequestMapping("/{id}/edit")
    public ResponseEntity<?> Edit(@PathVariable Integer id, Model model) {
        OrderHeader orderHeader = em.find(OrderHeader.class, id);
        List<Customer> customers = em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
        model.addAttribute("customers", customers);
        model.addAttribute("orderHeader", orderHeader);
        return ResponseEntity.ok(model);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ResponseEntity<?> Update(@RequestBody OrderHeader orderHeader, BindingResult result, Model model) { 
        if (result.hasErrors()) {
            return Util.getErrors(result.getFieldErrors());
        }
        OrderHeader item = em.find(OrderHeader.class, orderHeader.getId());
        item.setCustomer(orderHeader.getCustomer());
        item.setOrderDate(orderHeader.getOrderDate());
        em.persist(item);
        return ResponseEntity.ok().build();
    }

    @JsonView(Views.OrderHeaderDelete.class)
    @RequestMapping("/{id}/delete")
    public ResponseEntity<?> Delete(@PathVariable Integer id, Model model) {
        OrderHeader orderHeader = em.find(OrderHeader.class, id);
        model.addAttribute("orderHeader", orderHeader);
        return ResponseEntity.ok(model);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<?> DeleteConfirm(@PathVariable Integer id) {
        OrderHeader item = em.find(OrderHeader.class, id);
        em.remove(item);
        return ResponseEntity.ok().build();
    }  
}