package bu.COVIDApp.restservice.InfectedKeyUpload;

import bu.COVIDApp.Database.DatabaseInterface;
import bu.COVIDApp.Database.SQLKeySet.KeySetRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(path="/InfectedKey")
public class InfectedKeyUploadController {

    /**
     * An uploader specific to the currently selected backend runMode.
     * Handles uploading keys to whatever database is being used to store them
     */
    private final DatabaseInterface myInterface;

    /**
     * @param myReg an instance of KeySetRegistry that will be auto instantiated by spring boot for use by the key set uploader
     */
    InfectedKeyUploadController(KeySetRegistry myReg){
        this.myInterface = DatabaseInterface.InterfaceInitializer();
    }

    /**
     * Allows a user with credentials issued from /UploadCredentials to add keys to be marked as infected to the db
     * @param myKeys A list of InfectedKeys to be uploaded to whichever database is currently selected
     * @return a response to the user letting them know that the upload was successful
     * TODO: Add some credential check to this
     */
    @PostMapping()
    public @ResponseBody boolean PostInfectedKey(@RequestBody List<InfectedKeys> myKeys) {
        this.myInterface.addKeys(myKeys);
        return this.myInterface.uploadKeys();
    }
}
