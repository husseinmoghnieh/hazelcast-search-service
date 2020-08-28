package com.hazelcast.jam.search.invoicesearch.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceSearchResponse implements Serializable {

  private static final long serialVersionUID = -864793597336203315L;

  private List<Long> invoiceId;
  private BigDecimal totalAmount;
}
