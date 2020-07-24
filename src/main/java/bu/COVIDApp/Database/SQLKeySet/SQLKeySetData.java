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

    @Column(name = "myDay")
    private Integer day;

    public SQLKeySetData(){}

    public SQLKeySetData(String chirp,Integer day){
        this.chirp = chirp;
        this.day = day;
    }

    public SQLKeySetData(InfectedKey data){
        this.chirp = data.getChirp();
        this.day = data.getDay();
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

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }
}
