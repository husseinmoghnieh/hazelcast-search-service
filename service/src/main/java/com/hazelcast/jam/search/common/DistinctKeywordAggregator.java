package com.hazelcast.jam.search.common;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.hazelcast.aggregation.Aggregator;
import java.util.Map;
import java.util.Set;

public abstract class DistinctKeywordAggregator<I> extends Aggregator<I, Map<String, Set<String>>> {

  private SetMultimap<String, String> keywordMap = HashMultimap.create();

  public abstract Map<String, String> extractKeywords(I value);

  public SetMultimap<String, String> getKeywordMap() {
    return keywordMap;
  }

  @Override
  public void accumulate(I value) {
    Multimap<String, String> m = Multimaps.forMap(extractKeywords(value));
    keywordMap.putAll(Multimaps.filterValues(m, v -> v != null));
  }

  @Override
  public void combine(Aggregator aggregator) {
    this.keywordMap.putAll(this.getClass().cast(aggregator).getKeywordMap());
  }

  @Override
  public Map<String, Set<String>> aggregate() {
    return Multimaps.asMap(keywordMap);
  }
}
