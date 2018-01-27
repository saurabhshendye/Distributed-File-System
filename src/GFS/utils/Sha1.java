package GFS.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1 {

    /**
     * Constructor
     */
    public Sha1(){

    }

    /**
     *
     * @param data Data to be hashed
     * @return Hashed String
     * @throws NoSuchAlgorithmException
     */
    public String SHA1FromBytes(byte [] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        byte [] hash = digest.digest(data);
        BigInteger hashInt = new BigInteger(1, hash);

        return hashInt.toString(16);
    }
}
