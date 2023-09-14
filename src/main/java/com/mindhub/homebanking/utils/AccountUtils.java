package com.mindhub.homebanking.utils;

import java.util.Random;

public class AccountUtils {

    public static String getNumberAccount(){
        String numberAccount;
        Random random = new Random();
        numberAccount = "VIN-" + random.nextInt(99999999);
        return numberAccount;
    }
}
