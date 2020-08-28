package com.hazelcast.jam.search.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Component;

@Component
public class DomainMapper {
  private DozerBeanMapper dozerBeanMapper;
  private ObjectMapper objectMapper;

  public DomainMapper(DozerBeanMapper dozerBeanMapper, ObjectMapper objectMapper) {
    this.dozerBeanMapper = dozerBeanMapper;
    this.objectMapper = objectMapper;
  }

  /**
   * Map a single object.
   *
   * @param source Source object
   * @param targetType Class of target object
   * @param <S> Source
   * @param <T> Target
   * @return Target object
   */
  public <S, T> T map(S source, final Class<T> targetType) {
    return dozerBeanMapper.map(source, targetType);
  }
}
