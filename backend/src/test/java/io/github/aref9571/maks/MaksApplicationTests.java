package io.github.aref9571.maks;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MaksApplicationTests {

	@Test
	void contextLoads() {
	}

}
