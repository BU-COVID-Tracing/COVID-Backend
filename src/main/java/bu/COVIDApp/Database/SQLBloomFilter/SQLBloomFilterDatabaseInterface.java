package bu.COVIDApp.Database.SQLBloomFilter;

import bu.COVIDApp.Auxiliary.BloomFilter;
import bu.COVIDApp.CovidBackendApplication;
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
    // TODO: Test storing a byte array with each bloom filter index
    //private final int BUCKET_SIZE_BYTES = 1;


    public SQLBloomFilterDatabaseInterface(AppContext ctx){
        //Equivalent to @Autowire annotation but happens at runtime rather than compile time to prevent packaging issues
        keyReg = ctx.getContext().getBean(SQLBloomFilterRegistry.class);
        bloomFilter = new BloomFilter();
    }

    /**
     * Add keys to your bloom filter and upload any updated indices to the database
     * @param myKeys uploads all of the keys contained in myKeys to the db
     * @return true (Returns a response to the user)
     */
    @Override
    public boolean uploadKeys(List<InfectedKey> myKeys){
        //Update your local copy of the bloom filter with what is stored on the db
        final int BYTE_SIZE = 8;
        //Add the new keys to the filter
        HashMap<Integer,HashSet<Integer>> updatedIndices = this.bloomFilter.insert(myKeys);

        for (Integer day:updatedIndices.keySet()){
            //If the day the user is trying to upload is not within the previous 14 days, do not upload it
            if(day <= CovidBackendApplication.currentDay && day > CovidBackendApplication.currentDay-CovidBackendApplication.EXPOSURE_PERIOD){
                for (Integer bfIndex:updatedIndices.get(day)) {
                    //Shift a one over by the correct offset
                    byte mask = (byte) (1 << (BYTE_SIZE - 1 - (bfIndex % BYTE_SIZE)));
                    int bucket = bfIndex / BYTE_SIZE;

                    // TODO: I think collecting these for a bit and batching many together at once may be a better approach to this
                    keyReg.updateBloomFilter(bucket, mask, day);
                }
            }
        }

        return true;
    }

    /**
     * Reconstructs a bloom filter from the db and returns it as a byte array.
     * Bloom filter includes all data currently stored in the DB
     * @return A LinkedList of index/value objects
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
     * Reconstructs a bloom filter from the db and returns it as a byte array.
     * Bloom filter includes only data tagged with the day that is being queried for
     * @return A LinkedList of index/value objects
     */
    @Override
    public Object getData(Integer day) {
        //TODO: This query doesn't need to happen every time. Should be a background thread that occasionally updates
        ArrayList<SQLBloomFilterData> myData = keyReg.dayQuery(day);
        this.bloomFilter = new BloomFilter(myData);
        SQLBloomFilterResponse response = new SQLBloomFilterResponse(this.bloomFilter,0);
        return response.getDataContainer();
    }

    /**
     * @param myKeys A list of keys to check against your local version of the registry
     * @return true if any bloom filter match is found, false otherwise
     */
    @Override
    public Boolean checkKeys(ArrayList<InfectedKey> myKeys) {
        //TODO: This query doesn't need to happen every time. Should be a background thread that occasionally updates
        ArrayList<SQLBloomFilterData> myData = (ArrayList<SQLBloomFilterData>) keyReg.findAll();
        this.bloomFilter = new BloomFilter(myData);

        return this.bloomFilter.findMatches(myKeys);
    }

    /**
     * @param cutoff All keys older than cutoff will be removed from the db
     */
    @Override
    public void purgeOldKeys(Integer cutoff) {
        keyReg.purgeOldKeys(CovidBackendApplication.currentDay - cutoff);
    }
}
