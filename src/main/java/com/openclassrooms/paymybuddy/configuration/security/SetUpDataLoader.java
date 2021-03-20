package com.openclassrooms.paymybuddy.configuration.security;

import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import com.openclassrooms.paymybuddy.repository.UserAccountDAO;
import com.openclassrooms.paymybuddy.service.RoleService;
import com.openclassrooms.paymybuddy.service.UserAccountService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class SetUpDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private UserAccountService userAccountService;

    private RoleService roleService;

    private PasswordEncoder passwordEncoder;

    public SetUpDataLoader(final UserAccountService pUserAccountService,
                           final RoleService pRoleService,
                           final PasswordEncoder pPasswordEncoder) {
        userAccountService = pUserAccountService;
        roleService = pRoleService;
        passwordEncoder = pPasswordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!alreadySetup) {
            Role adminRole = createRoleIfNotFound("ROLE_ADMIN");
            Role userRole = createRoleIfNotFound("ROLE_USER");


            // TODO : suppress before ending from here...
            List<Role> roles = new ArrayList<>();
            roles.add(adminRole);
            roles.add(userRole);
            BankAccount bankAccount = new BankAccount("RIB", "Banque", "IBAN", "BIC");
            UserAccount userAccount = new UserAccount("FirstName", "LastName", "admin@test.com", passwordEncoder.encode("password"), roles, bankAccount, 0, new ArrayList<>(), new ArrayList<>());

            if (!userAccountService.findIfUserAccountExistsByEmail(userAccount.getEmail())) {
                userAccountService.saveUserAccount(userAccount);
            }

            List<Role> roles2 = new ArrayList<>();
            roles2.add(userRole);
            BankAccount bankAccount2 = new BankAccount("RIB", "Banque", "IBAN", "BIC");
            UserAccount userAccount2 = new UserAccount("FirstName2", "LastName2", "user@test.com", passwordEncoder.encode("password"), roles2, bankAccount2, 0, new ArrayList<>(), new ArrayList<>());

            if (!userAccountService.findIfUserAccountExistsByEmail(userAccount2.getEmail())) {
                userAccountService.saveUserAccount(userAccount2);
            }
            //TODO : ... until here.

            alreadySetup = true;
        }
    }

    @Transactional
    Role createRoleIfNotFound(String name) {
        Role role = roleService.findRoleByName(name);
        if (role == null) {
            role = new Role(name);
            roleService.saveRole(role);
        }
        return role;
    }
}
