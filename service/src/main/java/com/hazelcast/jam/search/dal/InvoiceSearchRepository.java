package com.hazelcast.jam.search.dal;

import com.hazelcast.jam.search.util.FileHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceSearchRepository {

  private static final String ACCOUNTS_BY_CLIENT_ID = FileHelper.readSQL("sql/ALL_INVOICES.sql");

  NamedParameterJdbcTemplate jdbcTemplate;
  DateTimeFormatter dtf = DateTimeFormat.forPattern("MM-dd-yyyy");

  @Autowired
  public void setDataSource(DataSource dataSource) {

    jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

  }

  public List<Invoice> getInvoices(Optional<List<Integer>> invoiceIds) {

    HashMap paramMap = new HashMap<>();
    if (invoiceIds.isEmpty())
    {
      paramMap.put("override", 0);
      paramMap.put("ids", "-1");
    } else{
      paramMap.put("override", 1);
      paramMap.put("ids", invoiceIds.get().stream().map(Object::toString).collect(Collectors.joining(",")));
    }


    return jdbcTemplate
        .query(ACCOUNTS_BY_CLIENT_ID, paramMap, new ResultSetExtractor<List<Invoice>>() {
          @Override
          public List<Invoice> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List invoices = new LinkedList();
            while (rs.next()) {
              Invoice invoice = new Invoice();
              invoice.setInvoiceId(rs.getLong("invoice_id"));
              invoice.setInvoiceNumber(rs.getString("invoice_number"));
              invoice.setAmount(rs.getBigDecimal("amount"));
              invoice.setPayorName(rs.getString("payor_name"));
              invoice.setPayeeName(rs.getString("payee_name"));
              invoices.add(invoice);
            }

            return invoices;
          }

        });

  }

}
