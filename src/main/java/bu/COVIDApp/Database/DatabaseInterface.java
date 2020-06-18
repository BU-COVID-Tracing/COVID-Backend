package bu.COVIDApp.Database;

import bu.COVIDApp.CovidBackendApplication;
import bu.COVIDApp.Database.SQLKeySet.SQLKeySetDatabaseInterface;
import bu.COVIDApp.Database.BloomFilterKV.BloomFilterKVDatabaseInterface;
import bu.COVIDApp.restservice.ContactCheck.RegistryGetResponse;
import bu.COVIDApp.restservice.InfectedKeyUpload.InfectedKeys;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class DatabaseInterface {
    protected LinkedList<InfectedKeys> uploadKeys;

    //////////////////
    //Initialization//
    //////////////////

    /**
     * The default constructor will be called when the uploader is @Autowired in the InfectedKeyUploadController
     */
    public DatabaseInterface(){
        this.uploadKeys = new LinkedList<>();
    }

    /**
     * Initialize the relevant DatabaseInterface child based on the value of CovidAppApplication.myRunMode
     * @return An instantiated child class of DatabaseInterface
     */
    public static DatabaseInterface InterfaceInitializer(){
        DatabaseInterface myInterface = null;

        switch (CovidBackendApplication.myRunMode){
            case SQLKeySet:
                myInterface = new SQLKeySetDatabaseInterface();
                break;
            case BloomFilterKV:
                myInterface = new BloomFilterKVDatabaseInterface();
                break;
            default:
                //This should never happen. Error should be reported when starting the application if a run mode was not selected
                System.out.println("A get to contactCheck request was made but a run mode has not been selected");
                System.exit(1);
        }

        return myInterface;
    }

    ///////////////
    //Key Uploads//
    ///////////////

    /**
     * Add a list of keys to the keys to be uploaded
     * @param myKeys the keys you would like to be uploaded on the next call of uploadKeys
     */
    public void addKeys(List<InfectedKeys> myKeys){
        this.uploadKeys.addAll(myKeys);
    }

    /**
     * Uploads the keys to the registry in the form required for that specific dbSchema
     * @return true if the upload completed successfully. False otherwise
     */
     public abstract boolean uploadKeys();

    /////////////////
    //Key Downloads//
    /////////////////

    /**
     * Retrieves an object(depends on the accessor) from the DB (or uses a cached local copy) for the user so that they
     * can check for potential contact on their local device
     */
    public abstract RegistryGetResponse getKeys();

    /**
     * Does a check on the server(here) and returns boolean letting the user know if contact has been found or not
     * @param myKeys A list of keys to check against your local version of the registry
     * @return true if a matching key was found, false otherwise
     */
    public abstract Boolean checkKeys(ArrayList<Object> myKeys);



    /////////////////
    //Authorization//
    /////////////////

    /**
     * check if the authorization key in myKeys is valid
     * @return true if authorization was successful, false otherwise
     * TODO: Implement this
     */
    protected boolean authorize(){
        return true;
    }

}
