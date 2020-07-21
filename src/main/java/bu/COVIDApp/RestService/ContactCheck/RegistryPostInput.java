package bu.COVIDApp.RestService.ContactCheck;

import bu.COVIDApp.RestService.InfectedKeyUpload.InfectedKey;

import java.util.ArrayList;

/**
 * The object that holds the array of keys and authorization parameters of the user wishing to check contact
 */
public class RegistryPostInput {
    private ArrayList<InfectedKey> keyArray;
    private String authorization;

    public RegistryPostInput(ArrayList<InfectedKey> keyArray,String authorization){
        this.keyArray = keyArray;
        this.authorization = authorization;
    }

    public ArrayList<InfectedKey> getKeys(){
        return keyArray;
    }

    public String getAuthorization(){
        return authorization;
    }
}
