package bu.COVIDApp.Database.SQLBloomFilter;

import bu.COVIDApp.Database.SQLKeySet.SQLKeySetData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called SQLBloomFilterRegistry
public interface SQLBloomFilterRegistry extends CrudRepository<SQLBloomFilterData,Integer>{
    /**
     * A query that updates the contents of a bucket by ORing it with a bitmask
     * @param bucket The index of the bucket you would like to update in the bloom filter
     * @param bitMask The bitmask you would like to or with the current contents of the bucket
     * @param day UNUSED at the moment but will specify which filter to update
     */
    @Query(value = "UPDATE sqlbloom_filter,SET my_data=my_data|?2 ,WHERE my_index=?1", nativeQuery = true)
    void updateBloomFilter(int bucket,byte bitMask,int day);
}





