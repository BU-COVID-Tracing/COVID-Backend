package bu.COVIDApp.Database.KVBloomFilter;

import bu.COVIDApp.Database.DatabaseInterface;
import bu.COVIDApp.Database.SQLKeySet.SQLKeySetResponse;
import bu.COVIDApp.RestService.InfectedKeyUpload.InfectedKey;

import java.util.ArrayList;
import java.util.List;

//Trying to implement a bloom filter with a kv system might result in concurrency issues when
// querying->updating locally->pushing results back
public class KVBloomFilterDatabaseInterface extends DatabaseInterface {

    //How many BYTES should be stored at each key in the kv store (i.e. how many BYTES of the bloom filter at each key)
    // TODO: Convert to a byte array to test with larger buckets
    //private final int BUCKET_SIZE_BYTES = 1;

    /**
     * The user should pass in a bloom filter that was calculated on the client side and this filter should be XORed with
     * the current filter
     * @return true if the update was successfully made, false otherwise
     */
    @Override
    public boolean uploadKeys(List<InfectedKey> myKeys) {
        return false;
    }

    /**
     * @return
     */
    @Override
    public SQLKeySetResponse getData() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public SQLKeySetResponse getData(Integer day) {
        return null;
    }

    @Override
    public Boolean checkKeys(ArrayList<InfectedKey> myKeys) {
        return null;
    }

    @Override
    public void purgeOldKeys(Integer cutoff) {
    }
}
