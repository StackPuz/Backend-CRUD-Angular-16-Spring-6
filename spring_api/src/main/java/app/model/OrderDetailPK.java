package app.model;

import app.model.Views.*;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class OrderDetailPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView({OrderDetailCreate.class,OrderDetailEdit.class,OrderDetailDelete.class,OrderHeaderDetail.class})
    @NotNull
    @Column(name="order_id")
    private Integer orderId;

    @JsonView({OrderDetailCreate.class,OrderDetailEdit.class,OrderDetailDelete.class,OrderHeaderDetail.class})
    @NotNull
    private Short no;

    public OrderDetailPK() {
    }

    public OrderDetailPK(Integer orderId, Short no) {
        this.setOrderId(orderId);
        this.setNo(no);
    }

    public Integer getOrderId() {
        return this.orderId;
    }
    
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Short getNo() {
        return this.no;
    }
    
    public void setNo(Short no) {
        this.no = no;
    }


    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrderDetailPK)) {
            return false;
        }
        OrderDetailPK castOther = (OrderDetailPK)other;
        return (this.orderId == castOther.orderId) && (this.no == castOther.no);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + ((int) (this.orderId ^ (this.orderId >>> 32)));
        hash = hash * prime + ((int) (this.no ^ (this.no >>> 32)));
        return hash;
    }
}