package app.model;

import app.model.Views.*;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class UserRolePK implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name="user_id")
    private Integer userId;

    @JsonView({UserAccountDetail.class,UserAccountCreate.class,UserAccountEdit.class,UserAccountDelete.class})
    @NotNull
    @Column(name="role_id")
    private Integer roleId;

    public UserRolePK() {
    }

    public UserRolePK(Integer userId, Integer roleId) {
        this.setUserId(userId);
        this.setRoleId(roleId);
    }

    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return this.roleId;
    }
    
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }


    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserRolePK)) {
            return false;
        }
        UserRolePK castOther = (UserRolePK)other;
        return (this.userId == castOther.userId) && (this.roleId == castOther.roleId);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + ((int) (this.userId ^ (this.userId >>> 32)));
        hash = hash * prime + ((int) (this.roleId ^ (this.roleId >>> 32)));
        return hash;
    }
}