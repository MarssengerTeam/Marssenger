package team.mars.marssenger.util;

import java.util.zip.CRC32;

/**
 * Created by Noli on 27.12.2014.
 * ChecksumGenerator CRC32 for generating and validating checksums of diverse integrated data
 */
public class ChecksumGenerator {
    private  CRC32 crc32 = new CRC32();

    public  void add(int i){
        crc32.update(i);
    }
    public  void add(byte[] b){
        crc32.update(b);
    }
    public  void add(byte[] b, int ofst, int count ){
        crc32.update(b,ofst,count);
    }
    public void reset(){
        crc32.reset();
    }

    public long  checksum(){
        return crc32.getValue();
    }

    public boolean validateNew(byte[] b, byte[] c){     //checks 2 whole new calculated checksums for equality
        CRC32 tmp = crc32;  //save old state including possible checksumdata
        //
        reset();
        add(b);
        long l = checksum();
        reset();
        add(c);
        long m = checksum();
        reset();

        crc32 = tmp;        //recover old state
        return (l==m);
    }

    public boolean validateOld(byte[] b){ //checks the substaining value with a newly generated checksum
        CRC32 tmp = crc32;  //save old state including possible checksumdata
        //
        long l = crc32.getValue();
        reset();
        add(b);
        long m = crc32.getValue();
        reset();
        crc32 = tmp;        //recover old state
        return(l == m);
    }

    public boolean validateChksm(long l){
        return(checksum() == l);
    }
}
