package com.openclassrooms.paymybuddy.security;

import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.Privilege;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.UserAccount;
import com.openclassrooms.paymybuddy.repository.PrivilegeDAO;
import com.openclassrooms.paymybuddy.repository.RoleDAO;
import com.openclassrooms.paymybuddy.repository.UserAccountDAO;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class SetUpDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private UserAccountDAO userAccountDAO;

    private RoleDAO roleDAO;

    private PrivilegeDAO privilegeDAO;

    private PasswordEncoder passwordEncoder;

    public SetUpDataLoader(final UserAccountDAO pUserAccountDAO,
                           final RoleDAO pRoleDAO,
                           final PrivilegeDAO pPrivilegeDAO,
                           final PasswordEncoder pPasswordEncoder) {
        userAccountDAO = pUserAccountDAO;
        roleDAO = pRoleDAO;
        privilegeDAO = pPrivilegeDAO;
        passwordEncoder = pPasswordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!alreadySetup) {
            Privilege readAllData = createPrivilegeIfNotFound("READ_ALL_DATA");
            Privilege readRestrictedData = createPrivilegeIfNotFound("READ_RESTRICTED_DATA");
            Privilege writeOwnerData = createPrivilegeIfNotFound("WRITE_OWNER_DATA");

            List<Privilege> adminPrivileges = new ArrayList<>();
            adminPrivileges.add(readAllData);
            List<Privilege> userPrivileges = new ArrayList<>();
            userPrivileges.add(readRestrictedData);
            userPrivileges.add(writeOwnerData);

            Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
            Role userRole = createRoleIfNotFound("ROLE_USER", userPrivileges);


            // TODO : suppress before ending from here...
            List<Role> roles = new ArrayList<>();
            roles.add(adminRole);
            roles.add(userRole);
            BankAccount bankAccount = new BankAccount("RIB", "Banque", "IBAN", "BIC");
            UserAccount userAccount = new UserAccount("FirstName", "LastName", "admin@test.com", passwordEncoder.encode("password"), roles, bankAccount, 0, new ArrayList<>(), new ArrayList<>());

            if (!userAccountDAO.existsByEmail(userAccount.getEmail())) {
                userAccountDAO.save(userAccount);
            }

            List<Role> roles2 = new ArrayList<>();
            roles2.add(userRole);
            BankAccount bankAccount2 = new BankAccount("RIB", "Banque", "IBAN", "BIC");
            UserAccount userAccount2 = new UserAccount("FirstName2", "LastName2", "user@test.com", passwordEncoder.encode("password"), roles2, bankAccount2, 0, new ArrayList<>(), new ArrayList<>());

            if (!userAccountDAO.existsByEmail(userAccount2.getEmail())) {
                userAccountDAO.save(userAccount2);
            }
            //TODO : ... until here.

            alreadySetup = true;
        }
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeDAO.findByName(name);
        if(privilege == null) {
            privilege = new Privilege(name);
            privilegeDAO.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        Role role = roleDAO.findByName(name);
        if (role == null) {
            role = new Role(name, privileges);
            roleDAO.save(role);
        }
        return role;
    }
}
