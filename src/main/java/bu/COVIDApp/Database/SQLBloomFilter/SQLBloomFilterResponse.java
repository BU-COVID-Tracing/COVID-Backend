package bu.COVIDApp.Database.SQLBloomFilter;


import bu.COVIDApp.BloomFilter;

import java.util.LinkedList;


public class SQLBloomFilterResponse {

    //A class that represents an index value pair in a bloom filter
    private class indexValue{
        private int index;
        private byte value;

        public indexValue(int index,byte value){
            this.index = index;
            this.value = value;
        }

        public int getIndex() {
            return index;
        }

        public byte getValue() {
            return value;
        }

    }

    private LinkedList<indexValue> myData;

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
                myData.add(new indexValue(ii,data[ii]));
    }

    public LinkedList<indexValue> getDataContainer() {
        return myData;
    }
}
