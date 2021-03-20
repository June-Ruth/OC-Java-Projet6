package com.openclassrooms.paymybuddy;

import com.openclassrooms.paymybuddy.web.controller.AdminController;
import com.openclassrooms.paymybuddy.web.controller.TransferController;
import com.openclassrooms.paymybuddy.web.controller.ProfileController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PaymybuddyApplicationTests {

	@Autowired
	private TransferController transferController;

	@Autowired
	private ProfileController profileController;

	@Autowired
	private AdminController adminController;

	@Test
	void contextLoads() {
		assertNotNull(adminController);
		assertNotNull(transferController);
		assertNotNull(profileController);
	}

}
