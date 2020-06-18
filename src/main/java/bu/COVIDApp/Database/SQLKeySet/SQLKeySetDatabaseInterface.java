package bu.COVIDApp.Database.SQLKeySet;

import bu.COVIDApp.Database.DatabaseInterface;
import bu.COVIDApp.restservice.ContactCheck.RegistryGetResponse;
import bu.COVIDApp.restservice.InfectedKeyUpload.InfectedKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SQLKeySetDatabaseInterface extends DatabaseInterface {
    @Autowired
    private KeySetRegistry keyReg;

    @Override
    public boolean uploadKeys() {
        for(InfectedKeys key:this.uploadKeys){
            KeySetData myData = new KeySetData(key.getChirp(),key.getTime());
            keyReg.save(myData);
        }

        //Clear the keys that you just uploaded from this array
        this.uploadKeys.clear();

        return true;
    }

    @Override
    public RegistryGetResponse getKeys() {
        //TODO: Check for null response from findAll
        ArrayList<KeySetData> myData = (ArrayList<KeySetData>)keyReg.findAll();
        return new RegistryGetResponse(myData);
    }

    @Override
    public Boolean checkKeys(ArrayList<Object> myKeys) {
        return null;
    }
}
