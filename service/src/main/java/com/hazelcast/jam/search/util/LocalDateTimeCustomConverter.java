package com.hazelcast.jam.search.util;

import org.dozer.DozerConverter;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LocalDateTimeCustomConverter extends DozerConverter<String, LocalDate> {

  public static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");;

  public LocalDateTimeCustomConverter() {
    super(String.class, LocalDate.class);
  }

  @Override
  public LocalDate convertTo(final String source, final LocalDate destination) {

    if (source == null) {
      return null;
    }

    return new LocalDate(source);
  }

  @Override
  public String convertFrom(LocalDate source, String destination) {
    if (source == null) {
      return null;
    }

    return source.toString();

  }

}
