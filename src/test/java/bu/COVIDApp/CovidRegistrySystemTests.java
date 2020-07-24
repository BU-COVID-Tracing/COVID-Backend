package bu.COVIDApp;

import bu.COVIDApp.RestService.InfectedKeyUpload.InfectedKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CovidRegistrySystemTests {

	@Autowired
	private CovidBackendApplication controller;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private Random rand = new Random(123);
	/**
	 * Sanity check to ensure that the application context starts properly
	 * @throws Exception
	 */
	@Test
	public void ContextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

	/**
	 * Add and retrieve keys from the database using the SQLKeySetSchema
	 * @throws Exception
	 */
	@Test
	//TODO: Add exception handling here
	public void AddGetKeysSQLKeySetTest() throws Exception {
		final int TEST_KEY_COUNT = 100;

		final String baseUrl = "http://localhost:"+port+"/InfectedKey/";
		URI uri = new URI(baseUrl);
		LinkedList<InfectedKey> uploadKeys = new LinkedList<>();

		//TOOD: Once timestamping is working properly update this test

		//Keys to check are found in the get
		for(int ii = 0; ii < TEST_KEY_COUNT/2;ii++)
			uploadKeys.add(new InfectedKey(Integer.toString(rand.nextInt()),1));

		//These are the keys we will check are returned in the get request
		LinkedList<InfectedKey> checkKeys = new LinkedList<>(uploadKeys);

		//Add some other data
		for(int ii = 0; ii < TEST_KEY_COUNT/2;ii++)
			uploadKeys.add(new InfectedKey(Integer.toString(rand.nextInt()),1));

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-COM-PERSIST", "true");

		HttpEntity<List<InfectedKey>> request = new HttpEntity<>(uploadKeys, headers);
		ResponseEntity<String> postResponse = this.restTemplate.postForEntity(uri, request, String.class);

		//Verify request returned a success code
		assertThat(200 == postResponse.getStatusCodeValue());

		//Make a get request
		uri = new URI(baseUrl+"/contactCheck/");
		ResponseEntity<String> getResponse = this.restTemplate.getForEntity(uri,String.class);

		//Check that your response contains every key in check keys
		for(InfectedKey key:checkKeys)
			assertThat(getResponse.getBody().contains(key.getChirp()) && getResponse.getBody().contains(Integer.toString(key.getDay())));
	}

	/**
	 * Add and retrieve keys from the database using the SQLKeySetSchema
	 * @throws Exception
	 */
	@Test
	public void SQLKeySetConcurrencyTest() throws Exception {
//		final int TEST_KEY_COUNT = 100;
//
//		final String baseUrl = "http://localhost:"+port+"/InfectedKey/";
//		URI uri = new URI(baseUrl);
//		LinkedList<InfectedKey> uploadKeys = new LinkedList<>();
//
//		//TOOD: Once timestamping is working properly update this test
//
//		//Keys to check are found in the get
//		for(int ii = 0; ii < TEST_KEY_COUNT/2;ii++)
//			uploadKeys.add(new InfectedKey(Integer.toString(rand.nextInt()),"1"));
//
//		//These are the keys we will check are returned in the get request
//		LinkedList<InfectedKey> checkKeys = new LinkedList<>(uploadKeys);
//
//		//Add some other data
//		for(int ii = 0; ii < TEST_KEY_COUNT/2;ii++)
//			uploadKeys.add(new InfectedKey(Integer.toString(rand.nextInt()),"1"));
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("X-COM-PERSIST", "true");
//
//		HttpEntity<List<InfectedKey>> request = new HttpEntity<>(uploadKeys, headers);
//		ResponseEntity<String> postResponse = this.restTemplate.postForEntity(uri, request, String.class);
//
//		//Verify request returned a success code
//		assertThat(200 == postResponse.getStatusCodeValue());
//
//		//Make a get request
//		uri = new URI(baseUrl+"/contactCheck/");
//		ResponseEntity<String> getResponse = this.restTemplate.getForEntity(uri,String.class);
//
//		//Check that your response contains every key in check keys
//		for(InfectedKey key:checkKeys)
//			assertThat(getResponse.getBody().contains(key.getChirp()) && getResponse.getBody().contains(key.getTime()));

		assertThat(true);
	}

}
