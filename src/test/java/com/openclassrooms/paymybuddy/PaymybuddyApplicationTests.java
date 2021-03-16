package com.openclassrooms.paymybuddy;

import com.openclassrooms.paymybuddy.web.controller.AdminController;
import com.openclassrooms.paymybuddy.web.controller.TransferController;
import com.openclassrooms.paymybuddy.web.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PaymybuddyApplicationTests {

	@Autowired
	private TransferController transferController;

	@Autowired
	private UserController userController;

	@Autowired
	private AdminController adminController;

	@Test
	void contextLoads() {
		assertNotNull(adminController);
		assertNotNull(transferController);
		assertNotNull(userController);
	}

}
