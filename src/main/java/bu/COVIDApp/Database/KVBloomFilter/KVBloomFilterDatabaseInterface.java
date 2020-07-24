package bu.COVIDApp.Database.KVBloomFilter;

import bu.COVIDApp.Database.DatabaseInterface;
import bu.COVIDApp.Database.SQLKeySet.SQLKeySetResponse;
import bu.COVIDApp.RestService.InfectedKeyUpload.InfectedKey;

import java.util.ArrayList;
import java.util.List;

public class KVBloomFilterDatabaseInterface extends DatabaseInterface {
    //TODO: Might be better to set these parameters at launch time rather than hard coding
    private final int HASH_CEILING = 2048;
    private final int NUM_HASHES = 3;

    //How many bits should be stored at each key in the kv store (i.e. how many bits of the bloom filter at each key)
    private final int BUCKET_SIZE = 8;

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

    /**
     * TODO: Put an actual hash function here that can be performed on the frontend as well(maybe murmur3)
     * @param inputKey the key that you would like to get the hash value of
     * @param modifier an integer that can be adjusted to give a second hash for the same input
     * @return an integer hash of your input
     */
    private Integer keyHash(String inputKey,int modifier){
        return (inputKey.hashCode() * 51 * modifier) % HASH_CEILING;
    }
}
