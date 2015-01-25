package team.mars.marssenger.datatype;

import java.io.Serializable;

/**
 * Created by Kern on 25.01.2015.
 */
public class TextMessage extends Message implements Serializable {

    @Override
    public void setMessage(String message) {
        super.setMessage(message);
    }

    public TextMessage(){}

    @Override
    public String getMessage(){
        return (String)super.getMessage();
    }

    @Override
    public int getType(){
        return 0;
    }
}
