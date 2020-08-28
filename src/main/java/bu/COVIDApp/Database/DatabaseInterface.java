package bu.COVIDApp.Database;

import bu.COVIDApp.CovidBackendApplication;
import bu.COVIDApp.Database.SQLBloomFilter.SQLBloomFilterDatabaseInterface;
import bu.COVIDApp.Database.SQLKeySet.SQLKeySetDatabaseInterface;
import bu.COVIDApp.Database.KVBloomFilter.KVBloomFilterDatabaseInterface;
import bu.COVIDApp.RestService.InfectedKeyUpload.InfectedKey;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class DatabaseInterface {

    //The number of hours between calling purgeOldKeys
    final static int CLEAN_FREQ = 24;

    //////////////////
    //Initialization//
    //////////////////

    /**
     * Initialize the relevant DatabaseInterface child based on the value of CovidAppApplication.myRunMode
     * Also launches the cleanup thread that will occasionally clean old records from the db
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

        //purge old entries once per 24 hours and increment the day count
        cleanupThread thread = new cleanupThread(myInterface);
        thread.start();

        return myInterface;
    }

    public static class cleanupThread extends Thread {
        private DatabaseInterface myInterface;

        cleanupThread(DatabaseInterface myInterface){
            this.myInterface = myInterface;
        }

        public void run(){
            while(true){
               System.out.println("System Current Day: "+CovidBackendApplication.currentDay);
                try {
                    TimeUnit.HOURS.sleep(CLEAN_FREQ);
//                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                this.myInterface.purgeOldKeys(CovidBackendApplication.EXPOSURE_PERIOD);

                //TODO: Should calculate the currentDay from some static point in time rather than just starting at 0 and incrementing
                CovidBackendApplication.currentDay++;
            }
        }
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


    /*
     * @param cutoff Any keys tagged with a date more than cutoff days before the current day will be erased from the database
     * @return Boolean true if the operations succeeds, false otherwise
     */
    public abstract void purgeOldKeys(Integer cutoff);

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
