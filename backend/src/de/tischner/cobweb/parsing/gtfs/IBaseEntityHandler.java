package de.tischner.cobweb.parsing.gtfs;

import java.io.IOException;

import org.onebusaway.csv_entities.EntityHandler;

public interface IBaseEntityHandler extends EntityHandler {
  void complete() throws IOException;
}
