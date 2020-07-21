package bu.COVIDApp.Database.SQLKeySet;

import bu.COVIDApp.RestService.InfectedKeyUpload.InfectedKey;

import javax.persistence.*;

/**
 * An entity representing the database schema for the schema that stores key,timestamp pairs in an sql database
 */
@Entity
@Table(name="SQLKeySet")
public class SQLKeySetData {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Column(name = "myChirp")
    private String chirp;

    //TODO: Make this an actual sql timestamp type
    @Column(name = "myTime")
    private String time;

    public SQLKeySetData(){}

    public SQLKeySetData(String chirp, String time){
        this.chirp = chirp;
        this.time = time;
    }

    public SQLKeySetData(InfectedKey data){
        this.chirp = data.getChirp();
        this.time = data.getTime();
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
