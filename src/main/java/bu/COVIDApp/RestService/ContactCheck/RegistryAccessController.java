package bu.COVIDApp.RestService.ContactCheck;

import bu.COVIDApp.Database.DatabaseInterface;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class RegistryAccessController {

    /**
     * Used for interacting with data that is stored on whichever database the current scheme uses
     */
    private final DatabaseInterface myInterface;

    public RegistryAccessController(){
        this.myInterface = DatabaseInterface.InterfaceInitializer();
    }

    /**
     * Do some type of access that gets the user information that allows them to check if they have keys that have been marked
     * as infected. Return value will depend on scheme in use
     * @param day An optional parameter that allows a user to query for data for only a single day
     * @return The object that will be sent to the user and will allow them to determine if they have made contact with an infected user
     */
    @GetMapping("/ContactCheck")
    public @ResponseBody Object getContactCheck(@RequestParam(defaultValue = "-1") int day){
        if(day == -1)
            return myInterface.getData();
        else
            return myInterface.getData(day);
    }

    /**
     * Allows a user to upload keys to the registry and a check is done on the backend to see if a match is found
     * @param UserInput an object containing the keys that the user would like to check
     * @return true or false depending on if contact was detected or not
     */
    @PostMapping("/ContactCheck")
    public @ResponseBody boolean postContactCheck(@RequestBody RegistryPostInput UserInput){
        return myInterface.checkKeys(UserInput.getKeys());
    }
}
