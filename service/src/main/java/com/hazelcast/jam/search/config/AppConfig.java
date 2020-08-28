package com.hazelcast.jam.search.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Collections;
import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedHeaders(
            "x-requested-with", "Origin", "Content-Type", "Accept", "Bearer", "Cache-Control")
        .allowedMethods("POST", "GET", "OPTIONS", "DELETE", "PATCH", "PUT");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

    registry
        .addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  @Bean
  public Validator validator() {
    return new LocalValidatorFactoryBean();
  }



  @Primary
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
//    mapper.registerModule(new JodaModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    return mapper;
  }

  @Bean
  public DozerBeanMapper dozerBeanMapper() {
    DozerBeanMapper mapper = new DozerBeanMapper();

    // it is necessary to load this mapping file in order to ensure joda datetime objects are handled properly
    // by dozer (i.e., failing to copy by reference causes the *current* time to get mapped rather than the time
    // indicated in the datetime objects)
    mapper.setMappingFiles(Collections.singletonList("dozer-mapping.xml"));
    return mapper;
  }

}
