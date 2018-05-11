package de.tischner.cobweb.util;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;

/**
 * Field naming strategy for member variables. It translates a field named like
 * <tt>mField</tt> to <tt>field</tt>.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 */
public final class MemberFieldNamingStrategy implements FieldNamingStrategy {

  /*
   * (non-Javadoc)
   * @see
   * com.google.gson.FieldNamingStrategy#translateName(java.lang.reflect.Field)
   */
  @Override
  public String translateName(final Field field) {
    // Remove the first character and lower the second (mField -> field)
    final char firstCharacter = Character.toLowerCase(field.getName().charAt(1));
    final String remainingName = field.getName().substring(2);

    return firstCharacter + remainingName;
  }

}
