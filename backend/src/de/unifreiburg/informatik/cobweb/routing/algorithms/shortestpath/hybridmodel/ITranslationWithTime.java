package de.unifreiburg.informatik.cobweb.routing.algorithms.shortestpath.hybridmodel;

/**
 * Mathematical translation of an element in one domain into another set.
 * Additionally a time information is attached to the translation.
 *
 * @author Daniel Tischner {@literal <zabuza.dev@gmail.com>}
 * @param <A> The type of elements in the domain of the translation
 * @param <B> The type of elements in the image of the translation
 */
@FunctionalInterface
public interface ITranslationWithTime<A, B> {
  /**
   * Translates the given element with the attached time.
   *
   * @param element The element to translate
   * @param time    The attached time
   * @return The element and time after applying the translation
   */
  B translate(A element, long time);
}
