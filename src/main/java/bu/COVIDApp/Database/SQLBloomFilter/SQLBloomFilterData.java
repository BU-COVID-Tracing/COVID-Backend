package bu.COVIDApp.Database.SQLBloomFilter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="SQLBloomFilter")
public class SQLBloomFilterData {
    //TODO: This should be a byte array for more flexibility/testing around bucket sizes
    @Column(name = "myData")
    private Byte data;

    @EmbeddedId
    private CompositeKey dayIndex;

    public SQLBloomFilterData(){
        this.data = 0;
        this.dayIndex = new CompositeKey();
    }

    public SQLBloomFilterData(Integer index,Byte data,Integer day){
        this.data = data;
        this.dayIndex = new CompositeKey(day,index);
    }

    public Byte getData() {
        return data;
    }

    public void setData(Byte data) {
        this.data = data;
    }

    public CompositeKey getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(CompositeKey dayIndex) {
        this.dayIndex = dayIndex;
    }


    @Embeddable
    public class CompositeKey implements Serializable{
        public CompositeKey(){
            this.day = 0;
            this.index = 0;
        }

        public CompositeKey(Integer day, Integer index){
            this.day = day;
            this.index = index;
        }

        @Column(name = "myDay",nullable = false)
        private Integer day;

        @Column(name = "myIndex",nullable = false)
        private Integer index;

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer key) {
            this.index = key;
        }
    }
}
