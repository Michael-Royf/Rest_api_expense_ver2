package com.michael.expense.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "tbl_users")
public class User implements UserDetails, Serializable {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    @Column(nullable = false, updatable = false)
    private Long id;
    @Column(nullable = false, updatable = false)
    private String userId;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false, name = "first_name")
    private String firstName;
    @Column(nullable = false, name = "last_name")
    private String lastName;
    @Column(nullable = false, name = "full_name")
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String profileImageUrl;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(name = "last_login_date", updatable = true)
    private Date lastLoginDate;
    @Column(name = "display_last_login_date", updatable = true)
    private Date displayLastLoginDate;

    @Column(name = "registration_date", nullable = false, updatable = false)
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private Timestamp registrationDate;

    @Column(name = "updated_date")
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private Date lastUpdateDate;

    private String role; //ROLES_USER, ROLE_ADMIN

    private String[] userAuthorities;

    @JsonIgnore
    private boolean isActive;
    @JsonIgnore
    private boolean isNotLocked;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return stream(this.getUserAuthorities())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isNotLocked(); //true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
      return  isActive();
    }
}
