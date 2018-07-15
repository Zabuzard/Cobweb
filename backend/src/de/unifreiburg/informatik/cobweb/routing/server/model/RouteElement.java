package de.unifreiburg.informatik.cobweb.routing.server.model;

import java.util.List;

import de.unifreiburg.informatik.cobweb.routing.model.graph.ETransportationMode;

/**
 * POJO that models a route element which can either represent a
 * {@link ERouteElementType#NODE} or {@link ERouteElementType#PATH}.<br>
 * <br>
 * A node consists of its coordinates and an optional name. A path consists of a
 * list of coordinates, a transportation mode and an optional name.<br>
 * <br>
 * Is used in a {@link Journey} and usually decoded into JSON.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class RouteElement {
  /**
   * A list of latitude and longitude coordinates belonging to this element.
   * Thus, the size of the arrays is <tt>2</tt>.
   */
  private List<float[]> mGeom;
  /**
   * The transportation mode belonging to this element.
   */
  private int mMode;
  /**
   * The name of this element or empty if not present.
   */
  private String mName;
  /**
   * The route element type of this element. Matches the values of the enum
   * {@link ERouteElementType}.
   */
  private int mType;

  /**
   * Creates a new route element. Should be used for
   * {@link ERouteElementType#PATH} since only they have a transportation mode.
   *
   * @param type The type of this route element
   * @param mode The transportation mode belonging to this element
   * @param name The name of this element or empty if not present
   * @param geom A list of latitude and longitude coordinates belonging to this
   *             element
   */
  public RouteElement(final ERouteElementType type, final ETransportationMode mode, final String name,
      final List<float[]> geom) {
    mName = name;
    mGeom = geom;

    setType(type);
    setMode(mode);
  }

  /**
   * Creates a new route element. Should be used for
   * {@link ERouteElementType#NODE} since they do not have a transportation
   * mode.
   *
   * @param type The type of this route element
   * @param name The name of this element or empty if not present
   * @param geom A list of latitude and longitude coordinates belonging to this
   *             element. The size of the arrays must be <tt>2</tt>.
   */
  public RouteElement(final ERouteElementType type, final String name, final List<float[]> geom) {
    this(type, ETransportationMode.IRRELEVANT, name, geom);
  }

  /**
   * Creates a new empty route element. Is used to construct the element via
   * reflection.
   */
  @SuppressWarnings("unused")
  private RouteElement() {
    // Empty constructor for construction through reflection
  }

  /**
   * Gets the list of latitude and longitude coordinates belonging to this
   * element. Thus, the size of the arrays is <tt>2</tt>.
   *
   * @return A list of latitude and longitude coordinates
   */
  public List<float[]> getGeom() {
    return mGeom;
  }

  /**
   * Gets the transportation mode belonging to this element.
   *
   * @return The transportation mode belonging to this element
   */
  public ETransportationMode getMode() {
    return ETransportationMode.fromValue(mMode);
  }

  /**
   * Gets the name of the element.
   *
   * @return The name of the element if present, else an empty text
   */
  public String getName() {
    return mName;
  }

  /**
   * Gets the type of this route element.
   *
   * @return The type of this element
   */
  public ERouteElementType getType() {
    return ERouteElementType.fromValue(mType);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("RouteElement [type=");
    builder.append(mType);
    builder.append(", mode=");
    builder.append(mMode);
    builder.append(", name=");
    builder.append(mName);
    builder.append(", geomSize=");
    builder.append(mGeom.size());
    builder.append("]");
    return builder.toString();
  }

  /**
   * Sets the transportation mode of this route element.
   *
   * @param mode The transportation mode to set
   */
  private void setMode(final ETransportationMode mode) {
    mMode = mode.getValue();
  }

  /**
   * Sets the type of this route element.
   *
   * @param type The type to set
   */
  private void setType(final ERouteElementType type) {
    mType = type.getValue();
  }
}
