package secure;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * Create encrypted key from string to encrypt url
 */

class MCrypt_ {
    private final IvParameterSpec iv_parameter;
    private final SecretKeySpec secret_key;
    private Cipher cipher;

    MCrypt_() {
        String secretKey = "29fb7bff6bafd1623ac0ba771f7af960";
        String iv = "ea2378e98ba83115";
        iv_parameter = new IvParameterSpec(iv.getBytes());
        secret_key = new SecretKeySpec(secretKey.getBytes(), "AES");
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    byte[] encrypt(String text) throws Exception {
        if (text == null || text.length() == 0)
            throw new Exception("Empty string");

        byte[] encrypted;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secret_key, iv_parameter);

            encrypted = cipher.doFinal(padString(text).getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    /*
    byte[] decrypt1(String code) throws Exception {
        if (code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, secret_key, iv_parameter);

            decrypted = cipher.doFinal(hexToBytes(code));
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }
*/

    static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }

        StringBuilder str = new StringBuilder();
        for (byte aData : data) {
            if ((aData & 0xFF) < 16)
                str.append("0").append(Integer.toHexString(aData & 0xFF));
            else
                str.append(Integer.toHexString(aData & 0xFF));
        }
        return str.toString();
    }

    /*
    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            sb.append((char)decimal);
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }
*/

    private static String padString(String source) {
        char paddingChar = ' ';
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        StringBuilder sourceBuilder = new StringBuilder(source);
        for (int i = 0; i < padLength; i++) {
            sourceBuilder.append(paddingChar);
        }
        source = sourceBuilder.toString();

        return source;
    }
}

