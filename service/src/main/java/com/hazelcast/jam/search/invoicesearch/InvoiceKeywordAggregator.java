package com.hazelcast.jam.search.invoicesearch;

import com.hazelcast.jam.search.common.DistinctKeywordAggregator;
import com.hazelcast.jam.search.invoicesearch.domain.InvoiceKeywordIndexEntry;
import java.util.HashMap;
import java.util.Map;

public class InvoiceKeywordAggregator
    extends DistinctKeywordAggregator<Map.Entry<Long, InvoiceKeywordIndexEntry>> {

  private static final long serialVersionUID = 1L;

  @Override
  public Map<String, String> extractKeywords(Map.Entry<Long, InvoiceKeywordIndexEntry> entry) {
    InvoiceKeywordIndexEntry value = entry.getValue();
    HashMap<String, String> newKeywords = new HashMap<>();

    newKeywords.put("invoiceNumber", value.getInvoiceNumber());
    newKeywords.put("amount", value.getAmount().toString());
    newKeywords.put("payorName", value.getPayorName());
    newKeywords.put("payeeName", value.getPayeeName());

    return newKeywords;
  }
}
