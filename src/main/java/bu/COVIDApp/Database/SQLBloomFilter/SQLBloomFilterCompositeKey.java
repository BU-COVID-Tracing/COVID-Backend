package bu.COVIDApp.Database.SQLBloomFilter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SQLBloomFilterCompositeKey implements Serializable {
    public SQLBloomFilterCompositeKey(){
        this.day = 0;
        this.index = 0;
    }

    public SQLBloomFilterCompositeKey(Integer day, Integer index){
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLBloomFilterCompositeKey that = (SQLBloomFilterCompositeKey) o;
        return day.equals(that.day) &&
                index.equals(that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, index);
    }
}