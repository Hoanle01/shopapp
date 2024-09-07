package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="fullname",length=100)
    private String fullName;
    @Column(name="phone_number",length=10,nullable = false)
    private String phoneNumber;
    @Column(name="address",length=200)
    private String address;

    @Column(name="password",length=200)
    private String password;
    @Column(name="is_active")
    private boolean active;
    @Column(name="date_of_birth")
    private Date date0fBirth;
    @Column(name="facebook_account_id")
    private int facebookAccountId;
    @Column(name="google_account_id")
    private int googleAccountId;
    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;
//lấy ra các cái quyền
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //để cái role tương thích authority phải convet
        List<GrantedAuthority> authoritiesList = new ArrayList<>();
        authoritiesList.add(new SimpleGrantedAuthority("ROLE_"+getRole().getName()));



        return List.of();
    }
//đây là nó hiểu dùng cái j để đăng nhập như bằng email,phonenumber
    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
