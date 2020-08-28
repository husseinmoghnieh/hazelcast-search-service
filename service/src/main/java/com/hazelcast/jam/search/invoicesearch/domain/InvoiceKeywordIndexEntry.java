package com.hazelcast.jam.search.invoicesearch.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hazelcast.jam.search.common.KeywordIndexDataSerializableFactory;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import lombok.Data;
import org.joda.time.LocalDate;

@Data
@JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
public class InvoiceKeywordIndexEntry
    implements com.hazelcast.nio.serialization.IdentifiedDataSerializable {

  private static final long serialVersionUID = -9080994776003312812L;

  private Long invoiceId;
  private String invoiceNumber;
  private BigDecimal amount;
  private String payorName;
  private String payeeName;


  @Override
  public void writeData(ObjectDataOutput out) throws IOException {

    out.writeLong(invoiceId);
    out.writeUTF(invoiceNumber);
    out.writeDouble(amount.doubleValue());
    out.writeUTF(payorName);
    out.writeUTF(payeeName);
  }

  @Override
  public void readData(ObjectDataInput in) throws IOException {

    invoiceId = in.readLong();
    invoiceNumber = in.readUTF();
    amount = new BigDecimal(
        in.readDouble(), MathContext.DECIMAL64
    ).setScale(2, RoundingMode.CEILING);
    payorName = in.readUTF();
    payeeName = in.readUTF();

  }

  @Override
  public int getFactoryId() {
    return KeywordIndexDataSerializableFactory.FACTORY_ID;
  }

  @Override
  public int getId() {
    return KeywordIndexDataSerializableFactory.INVOICE_KEYWORD_INDEX_ENTRY_TYPE;
  }
}
