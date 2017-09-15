package com.mjc.cryptochat.Utils;

import com.mjc.cryptochat.Activity.ChatActivity;

/**
 * Created by bijou on 15/09/2017.
 */

public class CryptManager {

    public CryptManager(){
        super();
    }

    public static String encryptMsg(String text, String key){
        char[] hintCharArray = key.toCharArray();
        char[] charArray = text.toCharArray();
        char[] finalCharArray = new char[charArray.length];

        int y = 0;
        for(int i = 0 ; i < charArray.length ; i++){
            if(i>=hintCharArray.length)y=0;

            int ascii = (int)charArray[i] + (int)hintCharArray[y];
            //If the ASCII nb is superior to 255 then go to the start
            if(ascii > 255){
                ascii -= 255;
            }
            finalCharArray[i] = (char)(ascii);
            y++;
        }
        return String.valueOf(finalCharArray);
    }
    public static String decryptMsg(String text, String key){
        char[] hintCharArray = key.toCharArray();
        char[] charArray = text.toCharArray();
        char[] finalCharArray = new char[charArray.length];

        int y = 0;
        for(int i = 0 ; i < charArray.length ; i++){
            if(i>=hintCharArray.length)y=0;
            int ascii = (int)charArray[i] - (int)hintCharArray[y];
            //If the ASCII nb is superior to 255 then go to the start
            if(ascii < 0){
                ascii += 255;
            }
            finalCharArray[i] = (char)(ascii);
            y++;
        }
        return String.valueOf(finalCharArray);
    }
}
