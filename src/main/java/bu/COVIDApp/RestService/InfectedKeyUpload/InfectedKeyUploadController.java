package bu.COVIDApp.RestService.InfectedKeyUpload;

import bu.COVIDApp.Database.DatabaseInterface;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import bu.COVIDApp.RestService.AppContext;

import java.util.List;

@Controller
@RequestMapping(path="/InfectedKey")
public class InfectedKeyUploadController {

    /**
     * An uploader specific to the currently selected backend runMode.
     * Handles uploading keys to whatever database is being used to store them
     */
    private DatabaseInterface myInterface;
    
    @Autowired
    private AppContext ctx;
    /**
     * Tells the post mapping if this is the first post call since initialization. If it is, initialize my interface
     */
    private boolean initialized;

    InfectedKeyUploadController(){
        this.initialized = false;
    }

    /**
     * Allows a user with credentials issued from /UploadCredentials to add keys to be marked as infected to the db
     * @param myKeys A list of InfectedKeys to be uploaded to whichever database is currently selected
     * @return a response to the user letting them know that the upload was successful
     * TODO: Add some credential check to this
     */
    @PostMapping()
    public @ResponseBody boolean PostInfectedKey(@RequestBody List<InfectedKey> myKeys) {
        //Some issue with the order of object creation requires that this happen here rather than in the constructor
        //Trying to initialize in the constructor leads to a nullptr even though interface initializer is a static function
        //This issue only appears when trying to package the program and run from a jar file
        if(!initialized){
            this.myInterface = DatabaseInterface.InterfaceInitializer(ctx);
            this.initialized = true;
        }

        return this.myInterface.uploadKeys(myKeys);
    }
}
