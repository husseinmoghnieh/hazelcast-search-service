package com.hazelcast.jam.search.util;

import org.dozer.DozerConverter;
import org.joda.time.DateTime;

public class DateTimeCustomConverter extends DozerConverter<DateTime, DateTime> {

  public DateTimeCustomConverter() {
    super(DateTime.class, DateTime.class);
  }

  @Override
  public DateTime convertTo(final DateTime source, final DateTime destination) {

    if (source == null) {
      return null;
    }

    return new DateTime(source);
  }

  @Override
  public DateTime convertFrom(final DateTime source, final DateTime destination) {

    if (source == null) {
      return null;
    }

    return new DateTime(source);
  }

}