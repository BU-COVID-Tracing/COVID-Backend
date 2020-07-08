package bu.COVIDApp.Database.SQLBloomFilter;

import bu.COVIDApp.BloomFilter;
import bu.COVIDApp.Database.DatabaseInterface;
import bu.COVIDApp.restservice.AppContext;
import bu.COVIDApp.restservice.InfectedKeyUpload.InfectedKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SQLBloomFilterDatabaseInterface extends DatabaseInterface {

    private final SQLBloomFilterRegistry keyReg;
    private BloomFilter bloomFilter;

    //How many BYTES should be stored at each key in the kv store (i.e. how many BYTES of the bloom filter at each key)
    // TODO: Only 1 works at the moment
    private final int BUCKET_SIZE_BYTES = 1;

    public SQLBloomFilterDatabaseInterface(){
        //Equivalent to @Autowire annotation but happens at runtime rather than compile time
        keyReg = AppContext.getContext().getBean(SQLBloomFilterRegistry.class);
        bloomFilter = new BloomFilter();
    }

    @Override
    //TODO: This function will create concurrency issues if multiple threads/machines are accessing the db
    //      The SQL operations here should be a transaction that rollsback and tries again if an update was made while processing
    //      Can also lock entries as a temporary solution with a lock column
    public boolean uploadKeys(List<InfectedKey> myKeys){
        //Update your local copy of the bloom filter with what is stored on the db
        ArrayList<SQLBloomFilterData> myData = (ArrayList<SQLBloomFilterData>) keyReg.findAll();
        this.bloomFilter = new BloomFilter(myData);

        //Add the new keys to the filter
        HashSet<Integer> updatedBins = this.bloomFilter.insert(myKeys);

        byte[] BFilter = this.bloomFilter.getFilterData();

        for(Integer index:updatedBins)
            keyReg.save(new SQLBloomFilterData(index,BFilter[index]));

        return true;
    }

    /**
     * Reconstructs a bloom filter from the db and returns it as a byte array
     * @return
     */
    @Override
    public Object getData() {
        ArrayList<SQLBloomFilterData> myData = (ArrayList<SQLBloomFilterData>) keyReg.findAll();
        this.bloomFilter = new BloomFilter(myData);
        return new SQLBloomFilterResponse(this.bloomFilter,0);
    }

    @Override
    public Boolean checkKeys(ArrayList<InfectedKey> myKeys) {
        ArrayList<SQLBloomFilterData> myData = (ArrayList<SQLBloomFilterData>) keyReg.findAll();
        this.bloomFilter = new BloomFilter(myData);

        return this.bloomFilter.findMatches(myKeys);
    }
}
