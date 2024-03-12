package app.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Menu {
    
    private String title;
    private String path;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String roles;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean show;
    @JsonIgnore
    private boolean active;

    public Menu() {}

    public String getTitle() {
        return this.title;
    }

    public String getPath() {
        return this.path;
    }
    
    public String getRoles() {
        return this.roles;
    }

    public boolean getShow() {
        return this.show;
    }

    public boolean getActive() {
        return this.active;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}