package bu.COVIDApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CovidAppApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void bloomFilter(){
		assertThat("Hello World").isEqualTo("Hello World");
	}

}
