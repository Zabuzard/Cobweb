package de.tischner.cobweb.util;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;

public final class MemberFieldNamingStrategy implements FieldNamingStrategy {

  @Override
  public String translateName(final Field field) {
    // Remove the first character and lower the second (mField -> field)
    final char firstCharacter = Character.toLowerCase(field.getName().charAt(1));
    final String remainingName = field.getName().substring(2);

    return firstCharacter + remainingName;
  }

}
