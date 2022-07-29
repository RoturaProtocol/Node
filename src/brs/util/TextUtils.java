package brs.util;

import brs.Constants;
import brs.fluxcapacitor.FluxValues;

import java.util.Locale;

public class TextUtils {
    public TextUtils() {
    }

    public static boolean isInAlphabet(String input) {
        if (input == null) return true;
        for (char c : input.toLowerCase(Locale.ENGLISH).toCharArray()) {
            if (!Constants.ALPHABET.contains(String.valueOf(c))) return false;
        }
        return true;
    }


    public static double getBlockTime(int blockHeight){
      if (blockHeight > 475112){
        return 2;
      }
      //fluxCapacitor.getValue(FluxValues.BLOCK_TIME);
      return   7;
    }
}
