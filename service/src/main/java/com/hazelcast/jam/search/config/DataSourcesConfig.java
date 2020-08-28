package com.hazelcast.jam.search.config;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
public class DataSourcesConfig {


  @Primary
  public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource jam) {
    return new NamedParameterJdbcTemplate(jam);
  }

  /* Embedded H2 db  */
  @ConfigurationProperties("spring.datasource")
  public DataSourceProperties h2DataSourceProperties() {
    return new DataSourceProperties();
  }

  public DataSource h2DataSource(DataSourceProperties payments) {
    return new EmbeddedDatabaseBuilder().setName("invoiceDB").setType(EmbeddedDatabaseType.H2)
        .addScript("classpath:data.sql").build();

  }

  public NamedParameterJdbcTemplate jdbcTemplate(DataSource h2) {
    return new NamedParameterJdbcTemplate(h2);
  }
}
