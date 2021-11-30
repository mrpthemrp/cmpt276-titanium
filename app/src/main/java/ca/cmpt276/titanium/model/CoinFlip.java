package ca.cmpt276.titanium.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * Represents a coin flip.
 *
 * @author Titanium
 */
public class CoinFlip {
  private final Coin childChosenSide;
  private final String date;
  private final Coin result;
  private final UUID childUniqueID;

  public CoinFlip(Coin childChosenSide, Coin result, UUID childUniqueID) {
    this.childChosenSide = childChosenSide;
    this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd, HH:mm:ss"));
    this.result = result;
    this.childUniqueID = childUniqueID;
  }

  public static Coin flipCoin() {
    Coin[] coinSides = Coin.values();
    Random random = new Random();
    return coinSides[random.nextInt(coinSides.length)];
  }

  public Coin getChildChosenSide() {
    return childChosenSide;
  }

  public String getDate() {
    return date;
  }

  public Coin getResult() {
    return result;
  }

  public UUID getChildUniqueID() {
    return childUniqueID;
  }
}
