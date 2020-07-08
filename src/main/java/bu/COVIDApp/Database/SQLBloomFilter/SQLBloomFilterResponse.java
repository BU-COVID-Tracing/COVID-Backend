package bu.COVIDApp.Database.SQLBloomFilter;


import bu.COVIDApp.BloomFilter;
import com.sun.tools.javac.util.Pair;

import java.util.LinkedList;


public class SQLBloomFilterResponse {


    private LinkedList<Pair<Byte,Integer>> myData;

    /**
     * @param bf The bloom filter to generate the response from
     * @param lastIndex the index with which to send any updates newer than (This will be used in the future to only send new data)
     */
    public SQLBloomFilterResponse(BloomFilter bf, int lastIndex){
        myData = new LinkedList<>();

        byte[] data = bf.getFilterData();

        //Store all indices that are not zero in myData
        for(int ii = 0; ii < data.length;ii++)
            if(data[ii] != 0)
                myData.add(new Pair<>(data[ii],ii));
    }

    public LinkedList<Pair<Byte,Integer>> getDataContainer() {
        return myData;
    }
}
