package com.hazelcast.jam.search.config;

import com.hazelcast.jam.search.common.KeywordIndexDataSerializableFactory;
import com.hazelcast.jam.search.invoicesearch.domain.InvoiceKeywordIndexEntry;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class HazelCastConfig {

  @Bean
  public IMap<Long, InvoiceKeywordIndexEntry> jamInvoiceHazelCastMap(Config cfg) {

    HazelcastInstance instance = Hazelcast.getOrCreateHazelcastInstance(cfg);

    IMap<Long, InvoiceKeywordIndexEntry> invoicesMap = instance.getMap("hazelcast_invoice_map");

    invoicesMap.addIndex("invoiceId", true);
    invoicesMap.addIndex("invoiceNumber", true);
    invoicesMap.addIndex("amount", true);
    invoicesMap.addIndex("payorName", true);
    invoicesMap.addIndex("payeeName", true);

    return invoicesMap;
  }

  @Configuration
  @Profile({"aws-ecs-cluster"})
  public static class AWSClusterHazelcastConfig {

    @Bean
    public Config hazelcastConfig(
        @Value("${hazelcast.aws.vpccidr}") String hazelVpcCidr,
        @Value("${hazelcast.aws.region}") String awsRegion,
        @Value("${hazelcast.aws.tag-key}") String tagKey,
        @Value("${hazelcast.aws.tag-value}") String tagValue) {


      Config config = new Config();
      config.setInstanceName("clustersearch");
      config.getSerializationConfig().addDataSerializableFactory(
          KeywordIndexDataSerializableFactory.FACTORY_ID,
          new KeywordIndexDataSerializableFactory()
      );

      List<String> interfaces = Arrays.stream(hazelVpcCidr.split(",")).collect(Collectors.toList());
      config.getNetworkConfig().getInterfaces().setEnabled(true).setInterfaces(interfaces);

      JoinConfig joinConfig = config.getNetworkConfig().getJoin();
      joinConfig.getMulticastConfig().setEnabled(false);
      joinConfig.getTcpIpConfig().setEnabled(false);
      joinConfig
          .getAwsConfig()
          .setEnabled(true)
          .setProperty("region", awsRegion)
          .setProperty("tag-key", tagKey)
          .setProperty("tag-value", tagValue);

      return config;
    }
  }



  @Configuration
  @Profile({"local"})
  public static class LocalHazelcastConfig {

    @Bean
    public Config hazelcastConfig() {


      Config config = new Config();
      config.setInstanceName("clustersearch");
      config.getSerializationConfig().addDataSerializableFactory(
          KeywordIndexDataSerializableFactory.FACTORY_ID,
          new KeywordIndexDataSerializableFactory()
      );
      return config;
    }
  }

}
