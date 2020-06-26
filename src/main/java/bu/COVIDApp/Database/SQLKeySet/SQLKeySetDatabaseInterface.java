package bu.COVIDApp.Database.SQLKeySet;

import bu.COVIDApp.Database.DatabaseInterface;
import bu.COVIDApp.restservice.AppContext;
import bu.COVIDApp.restservice.ContactCheck.RegistryGetResponse;
import bu.COVIDApp.restservice.InfectedKeyUpload.InfectedKey;

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
            SQLKeySetData myData = new SQLKeySetData(key.getChirp(),key.getTime());
            keyReg.save(myData);
        }

        return true;
    }

    @Override
    public RegistryGetResponse getData() {
        //TODO: Check for null response from findAll
        //TODO: This should actually be a range query over a certain number of days
        ArrayList<SQLKeySetData> myData = (ArrayList<SQLKeySetData>)keyReg.findAll();
        return new RegistryGetResponse(myData);
    }

    @Override
    public Boolean checkKeys(ArrayList<InfectedKey> myKeys) {
        return null;
    }
}
