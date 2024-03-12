package app.controller;

import app.util.Util;
import app.model.*;
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
@RequestMapping("/api/orderDetails")
@PreAuthorize("hasAnyAuthority('ADMIN','USER')")
public class OrderDetailController {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private HttpServletRequest request;

    @JsonView(Views.OrderDetailCreate.class)
    @RequestMapping("/create")
    public ResponseEntity<?> Create(Model model) {
        List<Product> products = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        model.addAttribute("products", products);
        return ResponseEntity.ok(model);
    }

    @RequestMapping(value="", method=RequestMethod.POST)
    public ResponseEntity<?> Create(@Valid @RequestBody OrderDetail item, BindingResult result, Model model) { 
        if (result.hasErrors()) {
            return Util.getErrors(result.getFieldErrors());
        }
        em.persist(item);
        return ResponseEntity.ok().build();
    }

    @JsonView(Views.OrderDetailEdit.class)
    @RequestMapping("/{orderId}/{no}/edit")
    public ResponseEntity<?> Edit(@PathVariable Integer orderId, @PathVariable Short no, Model model) {
        OrderDetail orderDetail = em.find(OrderDetail.class, new OrderDetailPK(orderId, no));
        List<Product> products = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        model.addAttribute("products", products);
        model.addAttribute("orderDetail", orderDetail);
        return ResponseEntity.ok(model);
    }

    @RequestMapping(value="/{orderId}/{no}", method=RequestMethod.PUT)
    public ResponseEntity<?> Update(@RequestBody OrderDetail orderDetail, BindingResult result, Model model) { 
        if (result.hasErrors()) {
            return Util.getErrors(result.getFieldErrors());
        }
        OrderDetail item = em.find(OrderDetail.class, orderDetail.getId());
        item.setProduct(orderDetail.getProduct());
        item.setQty(orderDetail.getQty());
        em.persist(item);
        return ResponseEntity.ok().build();
    }

    @JsonView(Views.OrderDetailDelete.class)
    @RequestMapping("/{orderId}/{no}/delete")
    public ResponseEntity<?> Delete(@PathVariable Integer orderId, @PathVariable Short no, Model model) {
        OrderDetail orderDetail = em.find(OrderDetail.class, new OrderDetailPK(orderId, no));
        model.addAttribute("orderDetail", orderDetail);
        return ResponseEntity.ok(model);
    }
    
    @RequestMapping(value="/{orderId}/{no}", method=RequestMethod.DELETE)
    public ResponseEntity<?> DeleteConfirm(@PathVariable Integer orderId, @PathVariable Short no) {
        OrderDetail item = em.find(OrderDetail.class, new OrderDetailPK(orderId, no));
        em.remove(item);
        return ResponseEntity.ok().build();
    }  
}