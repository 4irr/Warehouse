package com.coursework.Server.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    public User(String username, String password, String name, String surname, String email, String telNum, String role){
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.telNum = telNum;
        this.role = role;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userID;
    @Size(min = 5, message = "Минимальная длина - 5 символов")
    private String username;
    @Size(min = 5, message = "Минимальная длина - 5 символов")
    private String password;
    @Pattern(regexp = "^([а-яА-я]|[a-zA-z])*$", message = "Поле может содержать только буквы")
    private String name;
    @Pattern(regexp = "^([а-яА-я]|[a-zA-z])*$", message = "Поле может содержать только буквы")
    private String surname;
    @Pattern(regexp = "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$", message = "Неверный формат почты")
    private String email;
    @Pattern(regexp = "^(\\+375)([0-9]{9})$", message = "Неверный формат телефона")
    private String telNum;
    private String role;
    private boolean isBlocked;
    private int activity;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(role.equals("user"))
            return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        else
            return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isBlocked;
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
