package com.cova.taskmanager.users.model;

import com.frame.base.model.BaseModel;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User extends BaseModel implements UserDetails {
        public static final String USER_ID = "userId";
        public static final String USER_NAME = "name";
        public static final String USER_USERNAME = "username";
        public static final String USER_PASSWORD = "password";
        public static final String USER_EMAIL = "email";
        public static final String USER_TITLE = "title";
        public static final String USER_DESCRIPTION = "description";
        public static final String USER_STATUS = "status";
        // 1. Les Attributs
        private Long userId;
        private String name;
        private String username;
        private String email;
        private String password;

        private List<String> roles = new ArrayList<>(10);

        @Enumerated(EnumType.STRING)
        private TaskStatus status = TaskStatus.CREATED;

        public User() {}

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                List<String> roles = getRoles();
                if (roles != null){
                        roles.forEach(r -> grantedAuthorities.add(new SimpleGrantedAuthority(r)));
                }
                return grantedAuthorities;
        }

        @Override
        public String getPassword() {
             return this.password;
        }

        public void setPassword(String password) {
             this.password = password;
        }

        @Override
        public String getUsername()
        {
                String username = this.username;
                if (username == null || username.isEmpty()){
                        username = this.email;
                }
                return username ;
        }

        public boolean hasRole(String role){
                String prefix = "ROLE_";
                String roleTemplate = prefix+"%s";
                String rl = null;
                List<String> roles = getRoles();
                if (roles == null) roles = new ArrayList<>(0);
                for(String rle : roles){
                        rl = rle;
                        if (!role.startsWith(prefix)){
                                rl = String.format(roleTemplate,role);
                        }
                        if (rl.equalsIgnoreCase(rle)){
                                return true;
                        }
                }
                return  false;
        }

}
