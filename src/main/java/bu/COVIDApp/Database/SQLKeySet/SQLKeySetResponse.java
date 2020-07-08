package bu.COVIDApp.Database.SQLKeySet;


import java.util.ArrayList;


public class SQLKeySetResponse {
    private ArrayList<SQLKeySetData> DataContainer;

    public SQLKeySetResponse(ArrayList<SQLKeySetData> myDataContainer){
        this.DataContainer = myDataContainer;
    }

    public ArrayList<SQLKeySetData> getDataContainer() {
        return DataContainer;
    }

    public void setMyDataContainer(ArrayList<SQLKeySetData> myDataContainer) {
        this.DataContainer = myDataContainer;
    }
}
