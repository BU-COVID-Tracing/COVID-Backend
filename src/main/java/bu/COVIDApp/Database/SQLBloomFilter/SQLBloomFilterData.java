package bu.COVIDApp.Database.SQLBloomFilter;

import javax.persistence.*;

@Entity
@Table(name="SQLBloomFilter")
public class SQLBloomFilterData {
    //TODO: This should be a byte array for more flexibility/testing around bucket sizes
    @Column(name = "myData")
    private Byte data;

    @EmbeddedId
    private SQLBloomFilterCompositeKey dayIndex;

    public SQLBloomFilterData(){
        this.data = 0;
        this.dayIndex = new SQLBloomFilterCompositeKey();
    }

    public SQLBloomFilterData(Integer index,Byte data,Integer day){
        this.data = data;
        this.dayIndex = new SQLBloomFilterCompositeKey(day,index);
    }

    public Byte getData() {
        return data;
    }

    public void setData(Byte data) {
        this.data = data;
    }

    public SQLBloomFilterCompositeKey getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(SQLBloomFilterCompositeKey dayIndex) {
        this.dayIndex = dayIndex;
    }
}
