package bu.COVIDApp.Database;

import javax.persistence.*;

//TODO: Make these db components modular. Might not become clear how to do this until we start making the KV Bloom Filter db
/**
 * An entity representing the database schema for the schema that stores key,timestamp pairs in an sql database
 */
@Entity
@Table(name="key_store")
public class KeySetData {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Column(name = "chirp")
    private String chirp;

    //TODO: Make this an actual sql timestamp type
    @Column(name = "time")
    private String time;

    public KeySetData(){}

    public KeySetData(String chirp,String time){
        this.chirp = chirp;
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChirp() {
        return chirp;
    }

    public void setChirp(String key) {
        this.chirp = key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String timestamp) {
        this.time = timestamp;
    }
}
