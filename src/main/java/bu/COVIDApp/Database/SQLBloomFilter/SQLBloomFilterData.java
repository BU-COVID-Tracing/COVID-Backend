package bu.COVIDApp.Database.SQLBloomFilter;

import javax.persistence.*;

@Entity
@Table(name="SQLBloomFilter")
public class SQLBloomFilterData {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Column(name = "myIndex")
    private Integer index;

    //TODO: This should be a byte array for more flexibility/testing around bucket sizes
    @Column(name = "myData")
    private byte data;

    public SQLBloomFilterData(){
        this.index = 0;
        this.data = 0;
    }

    public SQLBloomFilterData(Integer index, byte data){
        this.index = index;
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer key) {
        this.index = key;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }
}
