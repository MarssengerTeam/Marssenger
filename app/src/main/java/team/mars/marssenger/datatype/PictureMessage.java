package team.mars.marssenger.datatype;

import java.io.Serializable;

/**
 * Created by Kern on 25.01.2015.
 */
public class PictureMessage extends Message implements Serializable {

    @Override
    public void setMessage(String path) {
        super.setMessage(path);
    }

    public PictureMessage(){}

    @Override
    public String getMessage(){
        return (String)super.getMessage();
    }

    @Override
    public int getType(){
        return 1;
    }









}
