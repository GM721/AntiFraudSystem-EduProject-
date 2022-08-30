package antifraud;

import antifraud.Enums.Regions;

import java.util.ArrayList;
import java.util.Arrays;

public class Valid {
    public static boolean checkIp (String ip) throws Exception {
        ArrayList<String> ipSplit = new ArrayList(Arrays.asList(ip.split("\\.")));
        if(ipSplit.size() == 4 && ip.charAt(0) != '.' && ip.charAt(ip.length() - 1) != '.'){;
            for(String i : ipSplit){
                int k = Integer.parseInt(i);
                if(k < 0 || k > 255) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean checkCard (String card) throws Exception{
        if(card.length() == 16){
            return isValidLuhn(card);
        }
        return false;
    }

    public static boolean isValidLuhn(String value) throws Exception{
        int sum = Character.getNumericValue(value.charAt(value.length() - 1));
        int parity = value.length() % 2;
        for (int i = value.length() - 2; i >= 0; i--) {
            int summand = Character.getNumericValue(value.charAt(i));
            if (i % 2 == parity) {
                int product = summand * 2;
                summand = (product > 9) ? (product - 9) : product;
            }
            sum += summand;
        }
        return (sum % 10) == 0;
    }
}
