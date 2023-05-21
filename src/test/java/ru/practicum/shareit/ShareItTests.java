package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@SpringBootTest
public
class ShareItTests {
	protected static Validator validator;

	@BeforeAll
	public static void beforeAllTests() {
		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			validator = validatorFactory.getValidator();
		}
	}

	@Test
	void contextLoads() {
	}

}