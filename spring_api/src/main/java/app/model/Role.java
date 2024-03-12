package app.model;

import app.model.Views.*;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name="Role")
@NamedQuery(name="Role.findAll", query="SELECT r FROM Role r")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView({UserAccountCreate.class,UserAccountEdit.class})
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @JsonView({UserAccountDetail.class,UserAccountCreate.class,UserAccountEdit.class,UserAccountDelete.class})
    @NotBlank
    @Size(max=50)
    private String name;

    @OneToMany(mappedBy="role")
    private List<UserRole> userRoles;

    public Role() {
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

    public List<UserRole> getUserRoles() {
        return this.userRoles;
    }
  
    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

}