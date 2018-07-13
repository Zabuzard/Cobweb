package de.tischner.cobweb.routing.algorithms.shortestpath.hybridmodel;

@FunctionalInterface
public interface ITranslationWithTime<A, B> {
  B translate(A element, long time);
}
