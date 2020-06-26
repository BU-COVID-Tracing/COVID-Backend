package bu.COVIDApp.Database.SQLBloomFilter;

import bu.COVIDApp.Database.SQLKeySet.SQLKeySetData;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called KeySetRegistry
public interface SQLBloomFilterRegistry extends CrudRepository<SQLBloomFilterData,Integer>{}





