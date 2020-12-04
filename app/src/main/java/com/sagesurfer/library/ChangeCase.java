package com.sagesurfer.library;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

/*
* This class converts input string as per desired  case for it
*/

public class ChangeCase {

    //Sentence case
    public static String toSentenceCase(String inputString) {
        StringBuilder result = new StringBuilder();
        if (inputString.length() == 0) {
            return result.toString();
        }
        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);
        result.append(firstCharToUpperCase);
        boolean terminalCharacterEncountered = false;
        char[] terminalCharacters = {'.', '?', '!'};
        for (int i = 1; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            if (terminalCharacterEncountered) {
                if (currentChar == ' ') {
                    result.append(currentChar);
                } else {
                    char currentCharToUpperCase = Character.toUpperCase(currentChar);
                    result.append(currentCharToUpperCase);
                    terminalCharacterEncountered = false;
                }
            } else {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result.append(currentCharToLowerCase);
            }
            for (char terminalCharacter : terminalCharacters) {
                if (currentChar == terminalCharacter) {
                    terminalCharacterEncountered = true;
                    break;
                }
            }
        }
        return result.toString();
    }

    //Title Case
    public static String toTitleCase(String str) {
        if (str == null) {
            return null;
        }
        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }
}
