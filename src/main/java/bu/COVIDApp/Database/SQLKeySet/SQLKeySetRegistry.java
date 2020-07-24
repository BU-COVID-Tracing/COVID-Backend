package bu.COVIDApp.Database.SQLKeySet;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface SQLKeySetRegistry extends CrudRepository<SQLKeySetData,Integer>{
    /**
     * A query that updates the contents of a bucket by ORing it with a bitmask
     * @param day UNUSED at the moment but will specify which filter to update
     */
    @Query(value = "SELECT * FROM sqlkey_set WHERE my_day=?1", nativeQuery = true)
    ArrayList<SQLKeySetData> dayQuery(int day);
}





