package jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Protocol {

    public static final byte DOLLAR_BYTE = '$';
    public static final byte ASTERISK_BYTE = '*';
    public static final byte PLUS_BYTE = '+';
    public static final byte MINUS_BYTE = '-';
    public static final byte COLON_BYTE = ':';
    public static final byte CR_BYTE = '\r';
    public static final byte LF_BYTE = '\n';

    public Protocol() {
    }

    public byte[] simpleStringResp(byte[] args) {

        int length = args.length;
        byte[] resp = new byte[length + 3];
        resp[0] = PLUS_BYTE;
        System.arraycopy(args, 0, resp, 1, length);
        resp[length + 1] = CR_BYTE;
        resp[length + 2] = LF_BYTE;

        return resp;
    }

    public byte[] bulkStringResp(byte[] args) {
        int length = args.length;
        String lengthStr = Integer.toString(length);
        int respLength = lengthStr.length() + length + 5;
        byte[] resp = new byte[respLength];
        resp[0] = DOLLAR_BYTE;
        System.arraycopy(lengthStr.getBytes(), 0, resp, 1, lengthStr.length());
        int index = 1 + lengthStr.length();
        resp[index++] = CR_BYTE;
        resp[index++] = LF_BYTE;
        System.arraycopy(args, 0, resp, index, length);
        index += length;
        resp[index++] = CR_BYTE;
        resp[index] = LF_BYTE;
        return resp;
    }
    
    public byte[] bulkStringResp(String args){
        return bulkStringResp(args.getBytes());
    }

    public byte[] simpleStringResp(String args){
        return simpleStringResp(args.getBytes());
    }

    
}
