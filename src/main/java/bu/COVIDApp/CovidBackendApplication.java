package bu.COVIDApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CovidBackendApplication {

	/**
	 * Describes which key storage strategy should be used
	 */
    public enum runMode{
    	SQLKeySet,
		BloomFilterKV
	}

	//TODO: Make it so that strategies that don't require a SQL database don't require valid sql credentials for a valid SQL database
	//		This is happening because spring is still trying to create a KeySetRegistry even though we aren't using it.
	public static runMode myRunMode;


	public static void main(String[] args) {
        try{
     		myRunMode = runMode.valueOf(args[0]);
		}catch(Exception e){
            System.out.println("Please specify a valid run mode (SQLKeySet,BloomFilterKV) with the following flag -Dspring-boot.run.arguments=\"YourRunMode\"");
            return;
		}

		SpringApplication.run(CovidBackendApplication.class, args);
	}

}
