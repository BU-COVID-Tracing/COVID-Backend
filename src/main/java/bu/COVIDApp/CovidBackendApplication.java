package bu.COVIDApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CovidBackendApplication {

	final int EXPOSURE_PERIOD = 14; //The total number of days worth of data that should be stored at any given time
									//After records are out of date they should be purged

	int currentDay = 0;

	/**
	 * Describes which key storage strategy should be used
	 */
    public enum runMode{
    	SQLKeySet,
		SQLBloomFilter,
		BloomFilterKV
	}

	public static runMode myRunMode = runMode.SQLKeySet;

	public static void main(String[] args) {
        try{
     		myRunMode = runMode.valueOf(args[0]);
		}catch(Exception e){
            System.out.println("No run mode specified. Defaulting to SQLKeySet. Choose an alternate run mode" +
					" with the following flag -Dspring-boot.run.arguments=\"YourRunMode\"");
		}

        //TODO: Add background thread that cleans older days out of the database occasionally
		SpringApplication.run(CovidBackendApplication.class, args);
	}
}
