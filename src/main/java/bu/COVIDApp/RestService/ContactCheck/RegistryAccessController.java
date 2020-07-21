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
     * as infected
     * @param authentication a user id to prevent/punish repeated queries
     * @return The object that will be sent to the user and will allow them to determine if they have made contact with an infected user
     */
    @GetMapping("/contactCheck")
    //TODO: The return type should be some abstract object that can be implemented for each method
    //TODO: Handle null response here
    public @ResponseBody Object getContactCheck (@RequestParam(value = "authentication", defaultValue = "") String authentication){
        return myInterface.getData();
    }

    /**
     * Allows a user to upload keys to the registry and a check is done on the backend to see if a match is found
     * @param UserInput an object containing the keys that the user would like to check
     * @return true or false depending on if contact was detected or not
     */
    @PostMapping("/contactCheck")
    public @ResponseBody boolean postContactCheck(@RequestBody RegistryPostInput UserInput){
        return myInterface.checkKeys(UserInput.getKeys());
    }

}
