package ca.cmpt276.titanium.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

// TODO: Remove/Use unused methods

/**
 * This class represents a single coin flip.
 */
public class CoinFlip {
    private static Children instance;
    private final UUID childUniqueID;
    private final Coin sideThatChildPicks;
    private final String timeOfFlip;
    private final Coin coinSideLandedOn;

    public CoinFlip(UUID childUniqueID, Coin sideThatChildPicks,
                    LocalDateTime timeOfFlip, Coin coinSideLandedOn) {
        this.childUniqueID = childUniqueID;
        this.sideThatChildPicks = sideThatChildPicks;
        this.timeOfFlip = timeOfFlip.format(DateTimeFormatter.ofPattern("d-MMM-uuuu, HH:mm:ss"));
        this.coinSideLandedOn = coinSideLandedOn;
    }

    public static Coin flipCoin() {
        Coin[] coins = Coin.values();
        Random random = new Random();
        return coins[random.nextInt(coins.length)];
    }

    public Coin getSideThatChildPicks() {
        return sideThatChildPicks;
    }

    public UUID getChildWhoPicksSideID() {
        return childUniqueID;
    }

    public String getTimeOfFlip() {
        return timeOfFlip;
    }

    public Coin getCoinSideLandedOn() {
        return coinSideLandedOn;
    }
}
