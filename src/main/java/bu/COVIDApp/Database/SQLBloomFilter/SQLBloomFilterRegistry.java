package bu.COVIDApp.Database.SQLBloomFilter;

import bu.COVIDApp.Database.SQLKeySet.SQLKeySetData;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;

public interface SQLBloomFilterRegistry extends CrudRepository<SQLBloomFilterData,Integer>{
    /**
     * A query that updates the contents of a bucket by ORing it with a bitmask
     * @param bucket The index of the bucket you would like to update in the bloom filter
     * @param bitMask The bitmask you would like to or with the current contents of the bucket
     * @param day UNUSED at the moment but will specify which filter to update
     */
    @Transactional
    @Modifying
    //Insert if the current index doesn't exist, else update the existing value by xoring with the new bit mask
    @Query(
            value = "INSERT INTO sqlbloom_filter (my_day,my_index,my_data) VALUES (?3,?1,?2) ON DUPLICATE KEY UPDATE my_data=my_data|VALUES(my_data)"
            , nativeQuery = true
    )
    void updateBloomFilter(int bucket,byte bitMask,int day);

    @Query(value = "SELECT * FROM sqlbloom_filter WHERE my_day=?1", nativeQuery = true)
    ArrayList<SQLBloomFilterData> dayQuery(int day);
}





