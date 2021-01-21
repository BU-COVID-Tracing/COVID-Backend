package bu.COVIDApp.Database.SQLBloomFilter;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;


@Repository
//TODO: Not sure if springboot has a way to do this but indexing db by days might make sense
public interface SQLBloomFilterRegistry extends CrudRepository<SQLBloomFilterData,Integer>{

    /**
     * Insert if the current index doesn't exist, else update the existing value by xoring with the new bit mask
     * @param bucket The index of the bucket you would like to update in the bloom filter
     * @param bitMask The bitmask you would like to or with the current contents of the bucket
     * @param day UNUSED at the moment but will specify which filter to update
     */
    @Transactional
    @Modifying
    @Query(
            value = "INSERT INTO sqlbloom_filter (my_day,my_index,my_data) VALUES (?3,?1,?2) ON DUPLICATE KEY UPDATE my_data=my_data|VALUES(my_data)"
            , nativeQuery = true
    )
    void updateBloomFilter(int bucket,byte bitMask,int day);

    /**
     * @param day The day you would like to query for data from
     * @return all rows where my_day == day
     */
    @Query(value = "SELECT * FROM sqlbloom_filter WHERE my_day=?1", nativeQuery = true)
    ArrayList<SQLBloomFilterData> dayQuery(int day);

    /**
     * @param day All entries in the database older than day will be deleted by this query
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM sqlkey_set WHERE my_day<?1", nativeQuery = true)
    void purgeOldKeys(int day);
}





