package com.openclassrooms.paymybuddy.configuration.security;

import com.openclassrooms.paymybuddy.constant.ErrorMessage;
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
    /**
     * @see UserAccountDAO
     */
    private UserAccountDAO userAccountDAO;

    /**
     * Autowired constructor.
     * @param pUserAccountDAO .
     */
    public UserDetailsServiceImpl(final UserAccountDAO pUserAccountDAO) {
        userAccountDAO = pUserAccountDAO;
    }

    /**
     * Define how username is defined.
     * @param email as username.
     * @return User.
     * @throws UsernameNotFoundException .
     */
    @Override
    public User loadUserByUsername(final String email)
            throws UsernameNotFoundException {
        UserAccount userAccount = userAccountDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        ErrorMessage.EMAIL_NOT_FOUND));

        return new User(userAccount.getEmail(),
                userAccount.getPassword(), true, true, true, true,
                getGrantedAuthorities(userAccount.getRoles()));
    }

    /**
     * Get list of authorities, here as role.
     * @param roles .
     * @return list of authorities.
     */
    private List<GrantedAuthority> getGrantedAuthorities(
            final Collection<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }
}
