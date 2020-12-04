package screen.messagelist;

import android.annotation.SuppressLint;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class KeyMaker_ {
    public static HashMap<String, String> getKey() {
        HashMap<String, String> params = new HashMap<>();
        String SecretKey = randomString();
        String key;
        byte[] infoBin;
        infoBin = SecretKey.getBytes();
        key = finalKey(getInt(byteToHex(infoBin)));
        params.put(General.TOKEN, key);
        params.put(General.KEY, _Base64.encode(SecretKey));
        return params;
    }

    public static String key(String message) {
        byte[] infoBin;
        infoBin = message.getBytes();
        message = finalKey(getInt(byteToHex(infoBin)));
        return message;
    }

    private static ArrayList<String> byteToHex(byte[] b) {
        int j;
        ArrayList<String> byteHex = new ArrayList<>();
        for (byte aB : b) {
            j = aB & 0xFF;
            byteHex.add(Integer.toHexString(j));
        }
        return byteHex;
    }

    private static ArrayList<String> getInt(ArrayList<String> hexArray) {
        ArrayList<String> evenHex = new ArrayList<>();
        ArrayList<String> oddHex = new ArrayList<>();
        for (int i = 0; i < hexArray.size(); i++) {
            String numStr = hexArray.get(i);
            for (int j = 0; j < numStr.length(); j++) {
                if (j % 2 == 0) {
                    evenHex.add(Character.toString(numStr.charAt(j)));
                } else {
                    oddHex.add(Character.toString(numStr.charAt(j)));
                }
            }
        }
        evenHex.addAll(oddHex);
        return evenHex;
    }

    private static String finalKey(ArrayList<String> finalHex) {
        StringBuilder strBuild = new StringBuilder();
        for (int i = 0; i < finalHex.size(); i++) {
            strBuild.append(finalHex.get(i));
        }
        return strBuild.toString();
    }

    @SuppressLint("TrulyRandom")
    private static String randomString() {
        char[] characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@!$."
                .toCharArray();
        Random random = new SecureRandom();
        char[] result = new char[10];
        for (int i = 0; i < result.length; i++) {
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }

}
