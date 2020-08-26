package bu.COVIDApp.Auxiliary;

import bu.COVIDApp.Database.SQLBloomFilter.SQLBloomFilterData;
import bu.COVIDApp.RestService.InfectedKeyUpload.InfectedKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BloomFilter {

    //TODO: Might be better to set these parameters at launch time rather than hard coding
    //TODO: Should do some math to determine good values for these parameters
    private final int HASH_CEILING = 2048;
    private final int NUM_HASHES = 3;
    private final int BYTE_SIZE = 8;

    // TODO: Might be more efficient to store only non-zero indices if on average the filter is not very full
    private byte[] filterData;

    public BloomFilter(){
        this.filterData = new byte[HASH_CEILING];
    }

    public BloomFilter(byte[] myData){
       this.filterData = myData;
    }

    public BloomFilter(List<InfectedKey> myData){
        this.filterData = new byte[HASH_CEILING];
        this.insert(myData);
    }

    /**
     * takes database data format and creates a bloom filter from it
     * @param myData the data you would like to add to the bloom filter
     */
    public BloomFilter(ArrayList<SQLBloomFilterData> myData){
        this.filterData = new byte[HASH_CEILING];

        //Put every item you get back in its correct place in the bloom filter
        //If an index is not found in the database it is assumed to be zero
        for(SQLBloomFilterData key:myData)
            this.filterData[key.getDayIndex().getIndex()] = key.getData();
    }

    /**
     * Insert a list of keys into the bloomFilter
     * @param myData The data you want to insert into your bloom filter
     * @return A Hashmap where keys are days and the values in the hash set that corresponds to each day is the updated indices for that bloom filter
     */
    public HashMap<Integer,HashSet<Integer>> insert(List<InfectedKey> myData){
        HashMap<Integer,HashSet<Integer>> returnSet = new HashMap<>();

        for(InfectedKey key:myData){
            //Add the set if it doesn't exist
            if(!returnSet.containsKey(key.getDay()))
                returnSet.put(key.getDay(),new HashSet<>());

            for(int ii = 1; ii <= NUM_HASHES;ii++){
                int hash = keyHash(key.getChirp(),ii);

                //Index into the group of 8 bits that you need to update. Set the relevant bit in that group
                byte offset = (byte)(BYTE_SIZE - 1 - (hash%BYTE_SIZE));
                filterData[hash/BYTE_SIZE] = (byte)(filterData[hash/BYTE_SIZE] | (1 << offset));

                //TODO: Should OPTIMIZE by only uploading new changes but having multiple filters for multiple days complicates things

                returnSet.get(key.getDay()).add(hash);
            }
        }

        return returnSet;
    }

    /**
     * As this is a bloom filter, there is a probability of false positives when finding matches
     * @param myData the list of keys you want to check
     * @return true if any match is found, false otherwise
     */
    public boolean findMatches(List<InfectedKey> myData){
        for(InfectedKey key:myData){
            boolean found = true;
            for(int ii = 1; ii <= NUM_HASHES;ii++){
                int hash = keyHash(key.getChirp(),ii);
                //Index into the group of 8 bits that you need to update. Set the relevant bit in that group
                byte offset = (byte)(BYTE_SIZE - 1 - (hash%BYTE_SIZE));

                //If you OR with the relevant bit and the number changes, the bit was not set
                if(filterData[hash/BYTE_SIZE] != (byte)(filterData[hash/BYTE_SIZE] | (1 << offset))){
                    found = false;
                    break;
                }
            }
            if(found)
                return true;
        }

        return false;
    }


    public byte[] getFilterData(){
        return filterData;
    }

    /**
     * THIS IS A FILLER HASH FUNCTION. THIS SHOULD NOT BE DEPLOYED UNTIL THIS IS CHANGED
     * TODO: Put an actual hash function here that can be performed on the frontend as well(maybe murmur3)
     * @param inputKey the key that you would like to get the hash value of
     * @param modifier an integer that can be adjusted to give a second hash for the same input
     * @return an integer hash of your input
     */
    private Integer keyHash(String inputKey,int modifier){
        return Math.abs(inputKey.hashCode() * 51 * modifier) % HASH_CEILING;
    }}
