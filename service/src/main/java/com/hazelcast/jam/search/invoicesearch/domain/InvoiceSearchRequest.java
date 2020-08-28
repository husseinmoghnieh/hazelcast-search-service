package com.hazelcast.jam.search.invoicesearch.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(
    ignoreUnknown = true
)
public class InvoiceSearchRequest implements Serializable {

  private static final long serialVersionUID = -1103467689647634740L;

  List<String> invoiceNumber;
  List<BigDecimal> amount;
  List<String> payorName;
  List<String> payeeName;
}
