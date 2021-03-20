package com.openclassrooms.paymybuddy.configuration.security;

import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.repository.UserAccountDAO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserAccountDAO userAccountDAO;

    public UserDetailsServiceImpl(final UserAccountDAO userAccountDAO) {
        this.userAccountDAO = userAccountDAO;
    }

    @Override
    public User loadUserByUsername(final String email) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountDAO.findByEmail(email);
        if (userAccount == null) {
            throw new UsernameNotFoundException("not found");
        } else {
            return new User(userAccount.getEmail(), userAccount.getPassword(), true, true, true, true, getGrantedAuthorities(userAccount.getRoles()));
        }
    }

    private List<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }
}
