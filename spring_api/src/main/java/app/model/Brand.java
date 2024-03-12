package app.model;

import app.model.Views.*;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name="Brand")
@NamedQuery(name="Brand.findAll", query="SELECT b FROM Brand b")
public class Brand implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView({BrandIndex.class,BrandDetail.class,BrandEdit.class,BrandDelete.class,ProductCreate.class,ProductEdit.class})
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @JsonView({BrandIndex.class,BrandDetail.class,BrandCreate.class,BrandEdit.class,BrandDelete.class,ProductIndex.class,ProductDetail.class,ProductCreate.class,ProductEdit.class,ProductDelete.class})
    @NotBlank
    @Size(max=50)
    private String name;

    @OneToMany(mappedBy="brand")
    private List<Product> products;

    public Brand() {
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

    public List<Product> getProducts() {
        return this.products;
    }
  
    public void setProducts(List<Product> products) {
        this.products = products;
    }

}