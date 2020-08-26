package bu.COVIDApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CovidBackendApplication {

	public static final int EXPOSURE_PERIOD = 14; //The total number of days worth of data that should be stored at any given time
									//After records are out of date they should be purged

	public static int currentDay = 0;

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
					" with the following flag -Dspring-boot.run.arguments={YOUR_RUN_MODE}");
		}

		SpringApplication.run(CovidBackendApplication.class, args);
	}
}
