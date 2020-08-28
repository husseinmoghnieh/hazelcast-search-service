package com.hazelcast.jam.search.invoicesearch.domain;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class InvoiceUpdateRequest implements Serializable {

  private static final long serialVersionUID = 8150076369985402720L;

  List<Integer> invoiceIds;
}
