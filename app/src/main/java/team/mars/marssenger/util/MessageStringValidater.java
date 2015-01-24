package team.mars.marssenger.util;

/**
 * Created by Kern on 24.01.2015.
 */
public class MessageStringValidater {
    //TODO expand if necessary
    public static boolean isValid(String txt){
        if(txt.length()<Math.pow(2,16)-1&&txt.length()!=0&&isNotOnlySpaces(txt)) { //TODO ist 2^16 ok oder mehr / weniger
            return true;
        }
      return false;
    }
    static boolean isNotOnlySpaces(String txt){
        for(int a=0;a<txt.length();a++){
            if(txt.charAt(a)!=' ')return true;
        }
        return false;
    }
}
