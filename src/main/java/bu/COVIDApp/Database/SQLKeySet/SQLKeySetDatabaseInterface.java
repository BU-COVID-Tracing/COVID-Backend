package bu.COVIDApp.Database.SQLKeySet;

import bu.COVIDApp.CovidBackendApplication;
import bu.COVIDApp.Database.DatabaseInterface;
import bu.COVIDApp.RestService.InfectedKeyUpload.InfectedKey;
import bu.COVIDApp.RestService.AppContext;

import java.util.ArrayList;
import java.util.List;


public class SQLKeySetDatabaseInterface extends DatabaseInterface {
   
 
   private final SQLKeySetRegistry keyReg;

   //@Autowired
   //private ApplicationContext applicationContext;
   public SQLKeySetDatabaseInterface(AppContext ctx){
        //Equivalent to a manual @Autowire at runtime to get around the packaging issue
	    keyReg = ctx.getContext().getBean(SQLKeySetRegistry.class);
    }

    @Override
    public boolean uploadKeys(List<InfectedKey> myKeys) {
        for(InfectedKey key:myKeys){
            // This makes sure that the db can't be filled with invalid data that will not be cleaned out for a long time or already expired data
            if(key.getDay() <= CovidBackendApplication.currentDay && key.getDay() > CovidBackendApplication.currentDay-CovidBackendApplication.EXPOSURE_PERIOD){
                SQLKeySetData myData = new SQLKeySetData(key.getChirp(),key.getDay());
                keyReg.save(myData);
            }
        }

        return true;
    }

    /**
     * Gets all data currently stored in the db. This should be CovidBackendApplication.EXPOSURE_PERIOD number of days
     */
    @Override
    public SQLKeySetResponse getData() {
	 ArrayList<SQLKeySetData> myData = (ArrayList<SQLKeySetData>)keyReg.findAll();
        return new SQLKeySetResponse(myData);
    }

    /**
     * Gets all of the keys associated with a particular day
     * @param day The day you would like to get keys from
     */
    @Override
    public SQLKeySetResponse getData(Integer day) {
        ArrayList<SQLKeySetData> myData = keyReg.dayQuery(day);
        return new SQLKeySetResponse(myData);
    }

    @Override
    public Boolean checkKeys(ArrayList<InfectedKey> myKeys) {
        ArrayList<SQLKeySetData> myData = (ArrayList<SQLKeySetData>)keyReg.findAll();
        for(InfectedKey key:myKeys){
            for(SQLKeySetData data:myData)
                if(data.getChirp().equals(key.getChirp()))
                    return true;
        }

        return false;
    }

    /**
     * @param cutoff The number of days after which keys should be purged from the database
     */
    @Override
    public void purgeOldKeys(Integer cutoff) {
        keyReg.purgeOldKeys(CovidBackendApplication.currentDay - cutoff);
    }
}
