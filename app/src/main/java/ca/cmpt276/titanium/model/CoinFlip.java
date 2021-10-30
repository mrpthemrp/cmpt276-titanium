package ca.cmpt276.titanium.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CoinFlip {
    private final Child childWhoPicksSide;
    private final Coin sideThatChildPicks;
    private final String timeOfFlip;
    private Coin coinSideLandedOn;

    public CoinFlip(Child childWhoPicksSide, Coin sideThatChildPicks,
                    LocalDateTime timeOfFlip, Coin coinSideLandedOn) {
        this.childWhoPicksSide = childWhoPicksSide;
        this.sideThatChildPicks = sideThatChildPicks;
        this.timeOfFlip = timeOfFlip.format(DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss"));
        this.coinSideLandedOn = coinSideLandedOn;
    }

    public Coin flipCoin() {
        Coin[] coins = Coin.values();
        Random random = new Random();
        return coins[random.nextInt(coins.length)];
    }

    public void setCoinSideLandedOn() {
        coinSideLandedOn = flipCoin();
    }

    public Child getChildWhoPicksSide() {
        return childWhoPicksSide;
    }

    public String getTimeOfFlip() {
        return timeOfFlip;
    }

    public Coin getCoinSideLandedOn() {
        return coinSideLandedOn;
    }
}
