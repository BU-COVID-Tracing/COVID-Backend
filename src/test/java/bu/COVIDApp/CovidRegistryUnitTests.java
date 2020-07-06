package bu.COVIDApp;

import bu.COVIDApp.restservice.InfectedKeyUpload.InfectedKey;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class CovidRegistryUnitTests {

	private Random rand = new Random(123);

	/**
	 * Check that inserting and checking for inserted values works properly
	 */
	@Test
	void BloomFilterBasicFunctionalityTest(){
		final int TEST_KEY_COUNT = 100;

		BloomFilter myBF = new BloomFilter();
		LinkedList<InfectedKey> testKeys = new LinkedList<>();

		//Add a random key to the testKey set
		for(int ii = 0; ii < TEST_KEY_COUNT;ii++)
			testKeys.add(new InfectedKey(Integer.toString(rand.nextInt()),"1"));

		myBF.insert(testKeys);

		//Check that each key is properly found in the filter
		for(InfectedKey key:testKeys){
		    LinkedList<InfectedKey> singleKey = new LinkedList<>();
		    singleKey.add(key);
			assertThat(myBF.findMatches(singleKey));
		}
	}

	/**
	 * Check that data that has not been inserted is not found when searched for
	 * (This test is probabilistic but seeded)
	 * TODO: This test should probably be converted to some kind of benchmark for false positive
	 * 		 rates for the current parameters
	 */
	@Test
	void BloomFilterNegativeResultTest(){
		final int TEST_KEY_COUNT = 100;

		BloomFilter myBF = new BloomFilter();
		LinkedList<InfectedKey> fillerKeys = new LinkedList<>();

		//Add a random key to the testKey set
		for(int ii = 0; ii < TEST_KEY_COUNT;ii++)
			fillerKeys.add(new InfectedKey(Integer.toString(rand.nextInt()),"1"));

		//Insert some random data into the filter
		myBF.insert(fillerKeys);

		//Check that items that weren't put in the filter don't return true
		LinkedList<InfectedKey> testKeys = new LinkedList<>();
		for(int ii = 0;ii < TEST_KEY_COUNT/10;ii++){
			InfectedKey newData  = new InfectedKey(Integer.toString(rand.nextInt()),"1");

			//If the key you've generated was one fo the filler keys, don't add it and perform an additional iteration
			if(fillerKeys.contains(newData)){
				ii--;
				continue;
			}else{
				testKeys.add(newData);
			}
		}

		//Check that matches are not found for keys that aren't in the filter
		assertThat(!myBF.findMatches(testKeys));
	}

	/**
	 * Check that inserting and checking for inserted values works properly
	 *
	 */
	@Test
	void BloomFilterConcurrencyTest(){
		final int THREADS = 20;
		final int KEYS_PER_THREAD = 5;

		BloomFilter myBF = new BloomFilter();
		ExecutorService executor = Executors.newFixedThreadPool(THREADS);

		//Generate data for each thread
		LinkedList<LinkedList<InfectedKey>> threadData = new LinkedList<>();
		for(int ii = 0; ii < THREADS;ii++ ){
			LinkedList<InfectedKey> testKeys = new LinkedList<>();
			//Add a random key to the testKey set
			for(int jj = 0; jj < KEYS_PER_THREAD;jj++)
				testKeys.add(new InfectedKey(Integer.toString(rand.nextInt()),"1"));

			threadData.add(testKeys);
		}

		//Run THREADS inserts of KEYS_PER_THREAD keys into the bloom filter in parallel
		for(LinkedList<InfectedKey> data:threadData){
			Runnable worker = new BFConcurrentUpdateThread(myBF,data);
			executor.execute(worker);
		}
		executor.shutdown();

		// Wait until all threads are finish
		while (!executor.isTerminated()) {}

		//Check that each key is properly found in the filter
		for(LinkedList<InfectedKey> data:threadData)
			assertThat(myBF.findMatches(data));
	}

	/**
	 * A thread that adds data to a BloomFilter
	 */
	class BFConcurrentUpdateThread extends Thread {
		private BloomFilter myFilter;
		private LinkedList<InfectedKey> myData;

		/**
		 * @param myFilter the BloomFilter to add data to in run()
		 * @param myData the data to add to the BloomFilter in run()
		 */
		public BFConcurrentUpdateThread (BloomFilter myFilter,LinkedList<InfectedKey> myData){
		    this.myData = myData;
			this.myFilter = myFilter;
		}

		public void run(){
			myFilter.insert(myData);
		}
	}

}
