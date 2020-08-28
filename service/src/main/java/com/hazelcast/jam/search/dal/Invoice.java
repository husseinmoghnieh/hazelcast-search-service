package com.hazelcast.jam.search.dal;

import java.math.BigDecimal;
import lombok.Data;
import org.joda.time.LocalDate;

@Data
public class Invoice {

  long invoiceId;
  String invoiceNumber;
  BigDecimal amount;
  String payorName;
  String payeeName;
  LocalDate invoiceDate;
}
