package app.model;

import app.model.Views.*;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name="UserAccount")
@NamedQuery(name="UserAccount.findAll", query="SELECT u FROM UserAccount u")
public class UserAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView({UserAccountIndex.class,UserAccountDetail.class,UserAccountCreate.class,UserAccountEdit.class,UserAccountDelete.class})
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @JsonView({ProductIndex.class,ProductDetail.class,ProductDelete.class,UserAccountIndex.class,UserAccountDetail.class,UserAccountCreate.class,UserAccountEdit.class,UserAccountDelete.class})
    @NotBlank
    @Size(max=50)
    private String name;

    @JsonView({UserAccountIndex.class,UserAccountDetail.class,UserAccountCreate.class,UserAccountEdit.class,UserAccountDelete.class})
    @NotBlank
    @Size(max=50)
    private String email;

    @Size(max=100)
    private String password;

    @Size(max=100)
    @Column(name="password_reset_token")
    private String passwordResetToken;

    @JsonView({UserAccountIndex.class,UserAccountDetail.class,UserAccountCreate.class,UserAccountEdit.class,UserAccountDelete.class})
    private Boolean active;

    @OneToMany(mappedBy="userAccount")
    private List<UserRole> userRoles;

    @OneToMany(mappedBy="userAccount")
    private List<Product> products;

    public UserAccount() {
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

    public String getEmail() {
        return this.email;
    }
  
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }
  
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordResetToken() {
        return this.passwordResetToken;
    }
  
    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public Boolean getActive() {
        return this.active;
    }
  
    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<UserRole> getUserRoles() {
        return this.userRoles;
    }
  
    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public List<Product> getProducts() {
        return this.products;
    }
  
    public void setProducts(List<Product> products) {
        this.products = products;
    }

}