package bu.COVIDApp.Database;

import bu.COVIDApp.CovidBackendApplication;
import bu.COVIDApp.Database.SQLBloomFilter.SQLBloomFilterDatabaseInterface;
import bu.COVIDApp.Database.SQLKeySet.SQLKeySetDatabaseInterface;
import bu.COVIDApp.Database.KVBloomFilter.KVBloomFilterDatabaseInterface;
import bu.COVIDApp.RestService.InfectedKeyUpload.InfectedKey;

import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseInterface {

    //////////////////
    //Initialization//
    //////////////////

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
            case SQLBloomFilter:
                myInterface = new SQLBloomFilterDatabaseInterface();
                break;
            case BloomFilterKV:
                myInterface = new KVBloomFilterDatabaseInterface();
                break;
        }

        return myInterface;
    }

    ///////////////
    //Key Uploads//
    ///////////////

    /**
     * @param myKeys uploads all of the keys contained in myKeys to the db
     * @return true if the upload completed successfully. False otherwise
     */
     public abstract boolean uploadKeys(List<InfectedKey> myKeys);

    /////////////////
    //Key Downloads//
    /////////////////

    /**
     * Retrieves an object(depends on the accessor) from the DB (or uses a cached local copy) for the user so that they
     * can check for potential contact on their local device
     */
    public abstract Object getData();

    /**
     * Retrieves an object(depends on the accessor) from the DB (or uses a cached local copy) for the user so that they
     * can check for potential contact on their local device. This overload takes a day and only returns data relevant to a specific day
     * @param day The day for which you would like to query keys
     */
    public abstract Object getData(Integer day);

    /**
     * Does a check on the server(here) and returns boolean letting the user know if contact has been found or not
     * @param myKeys A list of keys to check against your local version of the registry
     * @return true if a matching key was found, false otherwise
     */
    public abstract Boolean checkKeys(ArrayList<InfectedKey> myKeys);



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
