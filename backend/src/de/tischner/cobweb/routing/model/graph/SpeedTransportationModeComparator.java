package de.tischner.cobweb.routing.model.graph;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

/**
 * Comparator which compares transportation modes ascending in their typical
 * speed. For example a car will generally travel faster than a bike.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class SpeedTransportationModeComparator implements Comparator<ETransportationMode> {
  /**
   * The order defined by this comparator. Saved as map mapping modes to
   * numbers. The higher the number, the faster the mode.
   */
  private final Map<ETransportationMode, Integer> mOrder;

  /**
   * Creates a new comparator which compares transportation modes ascending in
   * their typical speed.
   */
  public SpeedTransportationModeComparator() {
    mOrder = new EnumMap<>(ETransportationMode.class);
    mOrder.put(ETransportationMode.FOOT, 0);
    mOrder.put(ETransportationMode.BIKE, 1);
    mOrder.put(ETransportationMode.TRAM, 2);
    mOrder.put(ETransportationMode.CAR, 3);
  }

  /**
   * Compares the given transportation modes ascending in their typical speed.
   * For example a car will generally travel faster than a bike.
   */
  @Override
  public int compare(final ETransportationMode first, final ETransportationMode second) {
    return Integer.compare(mOrder.get(first), mOrder.get(second));
  }

}
