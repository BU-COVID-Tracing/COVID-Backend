package bu.COVIDApp.Database.SQLBloomFilter;

import bu.COVIDApp.Auxiliary.BloomFilter;
import bu.COVIDApp.Database.DatabaseInterface;
import bu.COVIDApp.RestService.AppContext;
import bu.COVIDApp.RestService.InfectedKeyUpload.InfectedKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SQLBloomFilterDatabaseInterface extends DatabaseInterface {

    private final SQLBloomFilterRegistry keyReg;
    private BloomFilter bloomFilter;

    //How many BYTES should be stored at each key in the kv store (i.e. how many BYTES of the bloom filter at each key)
    // TODO: Only 1 works at the moment
    //private final int BUCKET_SIZE_BYTES = 1;

    public SQLBloomFilterDatabaseInterface(){
        //Equivalent to @Autowire annotation but happens at runtime rather than compile time
        keyReg = AppContext.getContext().getBean(SQLBloomFilterRegistry.class);
        bloomFilter = new BloomFilter();
    }

    @Override
    public boolean uploadKeys(List<InfectedKey> myKeys){
        //Update your local copy of the bloom filter with what is stored on the db
        final int BYTE_SIZE = 8;
        //Add the new keys to the filter
        HashMap<Integer,HashSet<Integer>> updatedIndices = this.bloomFilter.insert(myKeys);

        for (Integer day:updatedIndices.keySet()){
            for (Integer bfIndex:updatedIndices.get(day)) {
                //Shift a one over by the correct offset
                byte mask = (byte) (1 << (BYTE_SIZE - 1 - (bfIndex % BYTE_SIZE)));
                int bucket = bfIndex / BYTE_SIZE;

                // TODO: I think collecting these for a bit and batching many together at once may be a better approach to this
                keyReg.updateBloomFilter(bucket, mask, day);
            }
        }

        return true;
    }

    /**
     * Reconstructs a bloom filter from the db and returns it as a byte array
     * @return
     */
    @Override
    public Object getData() {
        //TODO: This doesn't need to happen every time. Should be a background thread that occasionally updates
        ArrayList<SQLBloomFilterData> myData = (ArrayList<SQLBloomFilterData>) keyReg.findAll();
        this.bloomFilter = new BloomFilter(myData);
        SQLBloomFilterResponse response= new SQLBloomFilterResponse(this.bloomFilter,0);
        return response.getDataContainer();
    }

    /**
     * Reconstructs a bloom filter from the db and returns it as a byte array
     * @return
     */
    @Override
    public Object getData(Integer day) {
        //TODO: This query doesn't need to happen every time. Should be a background thread that occasionally updates
        ArrayList<SQLBloomFilterData> myData = keyReg.dayQuery(day);
        this.bloomFilter = new BloomFilter(myData);
        SQLBloomFilterResponse response= new SQLBloomFilterResponse(this.bloomFilter,0);
        return response.getDataContainer();
    }

    @Override
    public Boolean checkKeys(ArrayList<InfectedKey> myKeys) {
        //TODO: This query doesn't need to happen every time. Should be a background thread that occasionally updates
        ArrayList<SQLBloomFilterData> myData = (ArrayList<SQLBloomFilterData>) keyReg.findAll();
        this.bloomFilter = new BloomFilter(myData);

        return this.bloomFilter.findMatches(myKeys);
    }
}
