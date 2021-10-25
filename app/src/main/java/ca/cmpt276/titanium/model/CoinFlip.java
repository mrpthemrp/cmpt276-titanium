package ca.cmpt276.titanium.model;

import java.time.LocalDateTime;
import java.util.Random;

public class CoinFlip {
    private Child childWhoPicksSide;
    private Coin sideThatChildPicks;
    private LocalDateTime timeOfFlip;
    private Coin coinSideLandedOn;

    public CoinFlip(Child childWhoPicksSide, Coin sideThatChildPicks,
                    LocalDateTime timeOfFlip, Coin coinSideLandedOn) {
        this.childWhoPicksSide = childWhoPicksSide;
        this.sideThatChildPicks = sideThatChildPicks;
        this.timeOfFlip = timeOfFlip;
        this.coinSideLandedOn = coinSideLandedOn;
    }

    public Coin flipCoin() {
        Coin[] coins = Coin.values();
        Random random = new Random();
        Coin coin = coins[random.nextInt(coins.length)];
        return coin;
    }

    public void setCoinSideLandedOn() {
        coinSideLandedOn = flipCoin();
    }

    public Child getChildWhoPicksSide() {
        return childWhoPicksSide;
    }

    public LocalDateTime getTimeOfFlip() {
        return timeOfFlip;
    }

    public Coin getCoinSideLandedOn() {
        return coinSideLandedOn;
    }
}
