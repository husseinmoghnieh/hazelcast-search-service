package com.hazelcast.jam.search.common;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class KeywordSearchQuery<K, Q> {

  private SetMultimap<String, Comparable<?>> terms = HashMultimap.create();

  public KeywordSearchQuery() {}

  public KeywordSearchQuery(Multimap<String, Comparable<?>> m) {
    terms.putAll(m);
  }

  public static <K, Q> KeywordSearchQuery<K, Q> createFromBeanObject(
      Object o, Class<?> targetClass) {
    try {
      PropertyDescriptor[] targetProps =
          Introspector.getBeanInfo(targetClass, Object.class).getPropertyDescriptors();
      String[] validAttributes =
          Stream.of(targetProps).map(PropertyDescriptor::getName).toArray(String[]::new);
      return createFromBeanObject(o, validAttributes);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <K, Q> KeywordSearchQuery<K, Q> createFromBeanObject(
      Object o, String... validAttributes) {
    final KeywordSearchQuery<K, Q> ksq = new KeywordSearchQuery<>();
    final HashSet<String> validKeySet = Sets.newHashSet(validAttributes);
    try {
      PropertyDescriptor[] props =
          Introspector.getBeanInfo(o.getClass(), Object.class).getPropertyDescriptors();
      for (PropertyDescriptor prop : props) {
        String key = prop.getName();
        Method getter = prop.getReadMethod();
        if (!"class".equals(key)
            && getter != null
            && (validAttributes.length == 0 || validKeySet.contains(key))) {
          Object val = getter.invoke(o);
          if (val instanceof List || val instanceof Set) {
            ksq.addTerms(key, (Iterable<? extends Comparable<?>>) val);
          } else {
            ksq.addTerm(key, (Comparable<?>) val);
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return ksq;
  }

  public KeywordSearchQuery(Map<String, Comparable<?>> m) {
    for (var e : m.entrySet()) {
      addTerm(e.getKey(), e.getValue());
    }
  }

  public KeywordSearchQuery(String k, Comparable<?>... v) {
    addTerms(k, Arrays.asList(v));
  }

  public void addTerm(String key, Comparable<?> value) {
    if (value != null) {
      terms.put(key, value);
    }
  }

  public void addTerms(String key, Comparable<?>... values) {
    if (values != null) {
      addTerms(key, Arrays.asList(values));
    }
  }

  public void addTerms(String key, Iterable<? extends Comparable<?>> values) {
    if (values != null) {
      terms.putAll(key, values);
    }
  }

  public Predicate<K, Q> getPredicate(String k) {
    var values = terms.get(k).stream().filter(v -> v != null).toArray(Comparable[]::new);
    if (values.length == 0) {
      return Predicates.alwaysTrue();
    }
    if (values.length == 1) {
      return Predicates.equal(k, values[0]);
    }
    return Predicates.in(k, values);
  }

  public Predicate<K, Q> asPredicate() {
    if (terms.isEmpty()) {
      return Predicates.alwaysTrue();
    }
    Set<String> keys = terms.keySet();
    Predicate<K, Q>[] predicates = keys.stream().map(this::getPredicate).toArray(Predicate[]::new);
    return Predicates.and(predicates);
  }
}
