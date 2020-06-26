package bu.COVIDApp.restservice.ContactCheck;


import bu.COVIDApp.BloomFilter;
import bu.COVIDApp.Database.SQLKeySet.SQLKeySetData;
import java.util.ArrayList;


public class RegistryGetResponse {
    private ArrayList<SQLKeySetData> myDataContainer;
    //TODO: Make this an abstract class so that these two data containers can be seperate
    private byte[] bloomFilter;

    public RegistryGetResponse(ArrayList<SQLKeySetData> myDataContainer){
        this.myDataContainer = myDataContainer;
        this.bloomFilter = null;
    }

    public RegistryGetResponse(BloomFilter myDataContainer){
        this.myDataContainer = null;
        this.bloomFilter = myDataContainer.getFilterData();
    }

    public ArrayList<SQLKeySetData> getMyDataContainer() {
        return myDataContainer;
    }

    public void setMyDataContainer(ArrayList<SQLKeySetData> myDataContainer) {
        this.myDataContainer = myDataContainer;
    }
}
