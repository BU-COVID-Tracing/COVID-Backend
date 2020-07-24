package bu.COVIDApp.RestService.InfectedKeyUpload;


/**
 * An object representing one key. This is the information that is stored in the database
 */
public class InfectedKey {
    private String chirp;
    private Integer day;

    public InfectedKey(String chirp, Integer day) {
        this.chirp = chirp;
        this.day = day;
    }

    public String getChirp() {
        return chirp;
    }

    public void setChirp(String chirp) {
        this.chirp = chirp;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day= day;
    }
}
