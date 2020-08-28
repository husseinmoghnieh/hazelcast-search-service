package com.hazelcast.jam.search.common;

import com.hazelcast.jam.search.invoicesearch.domain.InvoiceKeywordIndexEntry;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class KeywordIndexDataSerializableFactory implements DataSerializableFactory {

  public static final int FACTORY_ID = 1;
  public static final int INVOICE_KEYWORD_INDEX_ENTRY_TYPE = 1;

  @Override
  public IdentifiedDataSerializable create(int typeId) {

    if (typeId == INVOICE_KEYWORD_INDEX_ENTRY_TYPE) {
      return new InvoiceKeywordIndexEntry();
    } else {
      return null;
    }
  }
}
