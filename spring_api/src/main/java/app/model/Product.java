package app.model;

import app.model.Views.*;
import app.serializer.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import java.util.List;

@Entity
@Table(name="Product")
@NamedQuery(name="Product.findAll", query="SELECT p FROM Product p")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView({BrandEdit.class,OrderDetailCreate.class,OrderDetailEdit.class,ProductIndex.class,ProductDetail.class,ProductEdit.class,ProductDelete.class})
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @JsonView({BrandDetail.class,BrandEdit.class,BrandDelete.class,OrderDetailCreate.class,OrderDetailEdit.class,OrderDetailDelete.class,OrderHeaderDetail.class,OrderHeaderEdit.class,OrderHeaderDelete.class,ProductIndex.class,ProductDetail.class,ProductCreate.class,ProductEdit.class,ProductDelete.class})
    @NotBlank
    @Size(max=50)
    private String name;

    @JsonView({BrandDetail.class,BrandEdit.class,BrandDelete.class,ProductIndex.class,ProductDetail.class,ProductCreate.class,ProductEdit.class,ProductDelete.class})
    @NotNull
    private BigDecimal price;

    @JsonView({ProductIndex.class,ProductDetail.class,ProductCreate.class,ProductEdit.class,ProductDelete.class})
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="brand_id")
    private Brand brand;

    @JsonView({ProductIndex.class,ProductDetail.class,ProductCreate.class,ProductEdit.class,ProductDelete.class})
    @Size(max=50)
    private String image;

    @JsonView({ProductIndex.class,ProductDetail.class,ProductDelete.class})
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="create_user")
    private UserAccount userAccount;

    @JsonSerialize(using=DateTimeSerializer.class)
    @JsonDeserialize(using=DateTimeDeserializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="create_date")
    private Date createDate;

    @OneToMany(mappedBy="product")
    private List<OrderDetail> orderDetails;

    public Product() {
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

    public BigDecimal getPrice() {
        return this.price;
    }
  
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Brand getBrand() {
        return this.brand;
    }
  
    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getImage() {
        return this.image;
    }
  
    public void setImage(String image) {
        this.image = image;
    }

    public UserAccount getUserAccount() {
        return this.userAccount;
    }
  
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Date getCreateDate() {
        return this.createDate;
    }
  
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public List<OrderDetail> getOrderDetails() {
        return this.orderDetails;
    }
  
    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

}