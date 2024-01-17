package fr.uge.TheBigAventure.characters;

import java.util.ArrayList;
import java.util.Objects;

import fr.uge.TheBigAventure.gameObjects.Item;
import fr.uge.TheBigAventure.gameObjects.Item.ItemSkin;

public class TradeList {
  private final ArrayList<Trade> trades;

  public TradeList() {
    trades = new ArrayList<>();
  }

  public void add(Trade trade) {
    trades.add(trade);
  }

  public void remove(Trade trade) {
    trades.remove(trade);
  }

  public int size() {
    return trades.size();
  }

  public ArrayList<Trade> getTrades() {
    return trades;
  }

  public boolean availableTrade(ItemSkin item, ItemSkin price) {
    for (Trade trade : trades) {
      Item[] items = trade.getItems();
      if (items[0].getSkin() == item && items[1].getSkin() == price)
        return true;
    }
    return false;
  }

  public static TradeList valueOf(String string) {
    Objects.requireNonNull(string);
    TradeList tradeList = new TradeList();
    String[] trades = string.split(",");
    for (String trade : trades) {
      tradeList.add(Trade.valueOf(trade));
    }
    return tradeList;
  }

}
