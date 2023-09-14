package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.models.Card;

import java.util.Random;

public class CardUtils {
    public static String getNumberCard() {
        String numberCard;

            Random random = new Random();
            numberCard = random.nextInt(9999)
                    + "-" + random.nextInt(9999)
                    + "-" + random.nextInt(9999)
                    + "-" + random.nextInt(9999);

        return numberCard;
    }

    public static int getCvv(){
        Random random = new Random();
        return random.nextInt(999);
    }

}
