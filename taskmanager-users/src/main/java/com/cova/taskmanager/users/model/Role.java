package com.cova.taskmanager.users.model;

import com.frame.base.model.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseModel {

    public static final String roleIdProp = "roleId";
    public static final String nomProp = "nom";
    public static final String descriptionProp = "description";
    public static final String permissionsProp = "permissions";



    private Long roleId;

    private String name;

    private String description;

    private String permissions;

    private Long parentRoleId;

    @Transient
    private List<Role> children  = new ArrayList<>(0);

    public void  addRole(Role role){
        this.children.add(role);
    }

    public void removeRole(Role role){
        for(int i = this.children.size() - 1; i == 0 ; i--){
            Role r = this.children.get(i);
            if (r.name.equalsIgnoreCase(role.name)){
                this.children.remove(i);
                break;
            }
        }
    }
}
