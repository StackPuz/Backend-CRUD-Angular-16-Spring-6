package app.model;

import app.model.Views.*;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name="OrderDetail")
@NamedQuery(name="OrderDetail.findAll", query="SELECT o FROM OrderDetail o")
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView({OrderDetailCreate.class,OrderDetailEdit.class,OrderDetailDelete.class,OrderHeaderDetail.class})
    @EmbeddedId
    private OrderDetailPK id;

    @JsonView({OrderDetailCreate.class,OrderDetailEdit.class,OrderDetailDelete.class,OrderHeaderDetail.class})
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="order_id", insertable=false, updatable=false)
    private OrderHeader orderHeader;

    @JsonView({OrderDetailCreate.class,OrderDetailEdit.class,OrderDetailDelete.class,OrderHeaderDetail.class})
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="product_id")
    private Product product;

    @JsonView({OrderDetailCreate.class,OrderDetailEdit.class,OrderDetailDelete.class,OrderHeaderDetail.class})
    @NotNull
    private Short qty;

    public OrderDetail() {
    }
    
    public OrderDetailPK getId() {
        return this.id;
    }

    public void setId(OrderDetailPK id) {
        this.id = id;
    }
    public OrderHeader getOrderHeader() {
        return this.orderHeader;
    }
  
    public void setOrderHeader(OrderHeader orderHeader) {
        this.orderHeader = orderHeader;
    }

    public Product getProduct() {
        return this.product;
    }
  
    public void setProduct(Product product) {
        this.product = product;
    }

    public Short getQty() {
        return this.qty;
    }
  
    public void setQty(Short qty) {
        this.qty = qty;
    }

}