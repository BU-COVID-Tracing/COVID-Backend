package bu.COVIDApp.Database.SQLKeySet;

import bu.COVIDApp.Database.DatabaseInterface;
import bu.COVIDApp.RestService.AppContext;
import bu.COVIDApp.RestService.InfectedKeyUpload.InfectedKey;

import java.util.ArrayList;
import java.util.List;

public class SQLKeySetDatabaseInterface extends DatabaseInterface {
    private final SQLKeySetRegistry keyReg;

    public SQLKeySetDatabaseInterface(){
        //Equivalent to a manual @Autowire at runtime
        keyReg = AppContext.getContext().getBean(SQLKeySetRegistry.class);
    }

    @Override
    public boolean uploadKeys(List<InfectedKey> myKeys) {
        for(InfectedKey key:myKeys){
            SQLKeySetData myData = new SQLKeySetData(key.getChirp(),key.getDay());
            keyReg.save(myData);
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
}
