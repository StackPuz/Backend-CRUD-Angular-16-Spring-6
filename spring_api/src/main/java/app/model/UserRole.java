package app.model;

import app.model.Views.*;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name="UserRole")
@NamedQuery(name="UserRole.findAll", query="SELECT u FROM UserRole u")
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView({UserAccountDetail.class,UserAccountCreate.class,UserAccountEdit.class,UserAccountDelete.class})
    @EmbeddedId
    private UserRolePK id;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="user_id", insertable=false, updatable=false)
    private UserAccount userAccount;

    @JsonView({UserAccountDetail.class,UserAccountCreate.class,UserAccountEdit.class,UserAccountDelete.class})
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name="role_id", insertable=false, updatable=false)
    private Role role;

    public UserRole() {
    }
    
    public UserRolePK getId() {
        return this.id;
    }

    public void setId(UserRolePK id) {
        this.id = id;
    }
    public UserAccount getUserAccount() {
        return this.userAccount;
    }
  
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Role getRole() {
        return this.role;
    }
  
    public void setRole(Role role) {
        this.role = role;
    }

}