package com.terflo.helpdesk;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-dev.properties")
class HelpdeskApplicationTests {

	@Autowired
	BCryptPasswordEncoder encoder1;

	@Autowired
	BCryptPasswordEncoder encoder2;

	@SneakyThrows
	@Test
	void test() {
		System.out.println(encoder2.matches("qweqwe", "$2a$10$xhioIMcq3InvZb9gMoT62O/X.p5GWG5YdKZtzgq8880V11Bueqfam"));
	}

}
