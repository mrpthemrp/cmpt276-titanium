package ca.cmpt276.titanium.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * This class represents a single coin flip.
 */
public class CoinFlip {
    private final UUID pickerUniqueID;
    private final Coin chosenSide;
    private final String timeOfFlip;
    private final Coin result;

    public CoinFlip(UUID pickerUniqueID, Coin chosenSide, Coin result) {
        this.pickerUniqueID = pickerUniqueID;
        this.chosenSide = chosenSide;
        this.timeOfFlip = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy, HH:mm:ss"));
        this.result = result;
    }

    public static Coin flipCoin() {
        Coin[] coins = Coin.values();
        Random random = new Random();
        return coins[random.nextInt(coins.length)];
    }

    public UUID getPickerUniqueID() {
        return pickerUniqueID;
    }

    public Coin getChosenSide() {
        return chosenSide;
    }

    public String getTimeOfFlip() {
        return timeOfFlip;
    }

    public Coin getResult() {
        return result;
    }

    public LocalDateTime getLocalDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy, HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(timeOfFlip, formatter);
        return dateTime;
    }
}
