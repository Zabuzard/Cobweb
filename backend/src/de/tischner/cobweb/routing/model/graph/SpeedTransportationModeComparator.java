package de.tischner.cobweb.routing.model.graph;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public final class SpeedTransportationModeComparator implements Comparator<ETransportationMode> {

  private final Map<ETransportationMode, Integer> mOrder;

  public SpeedTransportationModeComparator() {
    mOrder = new EnumMap<>(ETransportationMode.class);
    mOrder.put(ETransportationMode.FOOT, 0);
    mOrder.put(ETransportationMode.BIKE, 1);
    mOrder.put(ETransportationMode.TRAM, 2);
    mOrder.put(ETransportationMode.CAR, 3);
  }

  @Override
  public int compare(final ETransportationMode first, final ETransportationMode second) {
    return Integer.compare(mOrder.get(first), mOrder.get(second));
  }

}
