package app.model;

import app.model.Views.*;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name="Customer")
@NamedQuery(name="Customer.findAll", query="SELECT c FROM Customer c")
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView({OrderHeaderCreate.class,OrderHeaderEdit.class})
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @JsonView({OrderHeaderIndex.class,OrderHeaderDetail.class,OrderHeaderCreate.class,OrderHeaderEdit.class,OrderHeaderDelete.class})
    @NotBlank
    @Size(max=50)
    private String name;

    @OneToMany(mappedBy="customer")
    private List<OrderHeader> orderHeaders;

    public Customer() {
    }
    
    public Integer getId() {
        return this.id;
    }
  
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
  
    public void setName(String name) {
        this.name = name;
    }

    public List<OrderHeader> getOrderHeaders() {
        return this.orderHeaders;
    }
  
    public void setOrderHeaders(List<OrderHeader> orderHeaders) {
        this.orderHeaders = orderHeaders;
    }

}