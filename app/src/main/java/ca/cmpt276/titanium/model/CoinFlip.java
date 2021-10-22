package ca.cmpt276.titanium.model;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * This class represents a coin flip for an individual round.
 */
public class CoinFlip {
    private enum Coin {
        HEADS,
        TAILS
    }
    private final Child childWhoFlips;
    private final LocalDateTime timeOfFlip;
    private final Coin coinSideLandedOn;

    // TODO: This constructor is just a placeholder so our code will compile, need to clarify coin
    // flip logic with Brian
    public CoinFlip(int uniqueId) {

    }

    public CoinFlip(Child childWhoFlips, LocalDateTime timeOfFlip, Coin coinSideLandedOn) {
        this.childWhoFlips = childWhoFlips;
        this.timeOfFlip = timeOfFlip;
        this.coinSideLandedOn = coinSideLandedOn;
    }

    public Coin flipCoin() {
        Coin[] coins = Coin.values();
        Random random = new Random();
        Coin coin = coins[random.nextInt(coins.length)];
        return coin;
    }

    public LocalDateTime getTimeOfFlip() {
        return timeOfFlip;
    }

    public Coin getCoinSideLandedOn() {
        return coinSideLandedOn;
    }
}
