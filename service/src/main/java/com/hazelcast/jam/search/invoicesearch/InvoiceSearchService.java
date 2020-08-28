package com.hazelcast.jam.search.invoicesearch;

import com.hazelcast.jam.search.common.DomainMapper;
import com.hazelcast.jam.search.common.KeywordSearchQuery;
import com.hazelcast.jam.search.dal.InvoiceSearchRepository;
import com.hazelcast.jam.search.invoicesearch.domain.InvoiceKeywordIndexEntry;
import com.hazelcast.jam.search.invoicesearch.domain.InvoiceSearchRequest;
import com.hazelcast.jam.search.invoicesearch.domain.InvoiceSearchResponse;
import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.core.IMap;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceSearchService {

  private DomainMapper domainMapper;
  private IMap<Long, InvoiceKeywordIndexEntry> invoiceDataMap;
  private InvoiceSearchRepository invoiceSearchRepository;

  private static final Integer OPS_CLIENT_ID = 0;


  @Autowired
  public InvoiceSearchService(
      IMap<Long, InvoiceKeywordIndexEntry> invoiceDataMap,
      InvoiceSearchRepository invoiceSearchRepository,
      DomainMapper objectMapper) {

    this.invoiceDataMap = invoiceDataMap;
    this.invoiceSearchRepository = invoiceSearchRepository;
    this.domainMapper = objectMapper;
  }

  public boolean refreshInvoices() {

    var invoices = invoiceSearchRepository.getInvoices(Optional.empty());

    if (invoices.size() > 0) {

      Map<Long, InvoiceKeywordIndexEntry> newMap =
          invoices
              .parallelStream()
              .map(e -> domainMapper.map(e, InvoiceKeywordIndexEntry.class))
              .collect(Collectors.toMap(e -> e.getInvoiceId(), e -> e));

      Predicate toEvict =
          Predicates.not(Predicates.in("__key", newMap.keySet().toArray(Long[]::new)));

      invoiceDataMap.removeAll(toEvict);

      invoiceDataMap.putAll(newMap);
    }

    return true;
  }

  public InvoiceSearchResponse searchInvoices(
      Integer page,
      Integer pageSize,
      Optional<String> sortBy,
      InvoiceSearchRequest invoiceSearchRequest) {

    Collection<InvoiceKeywordIndexEntry> searchInvoiceIds = null;

    Predicate<Long, InvoiceKeywordIndexEntry> searchPredicate =
        buildPredicateInv(invoiceSearchRequest);

    Comparator<Map.Entry<Long, InvoiceKeywordIndexEntry>> sortingOrder =
        new DescendingIdComparator();

    if (sortBy.isPresent() && sortBy.get().contains("amount")) {
      if (sortBy.get().contains("-")) {
        sortingOrder = new InvoiceAmountDescendingSortComparator();
      } else {
        sortingOrder = new InvoiceAmountAscendingSortComparator();
      }
    }

    if (pageSize == 0) {
      searchInvoiceIds = invoiceDataMap.values(searchPredicate);
    } else {
      PagingPredicate<Long, InvoiceKeywordIndexEntry> pagingPredicate =
          new PagingPredicate<>(searchPredicate, sortingOrder, pageSize);
      pagingPredicate.setPage(page);
      searchInvoiceIds = invoiceDataMap.values(pagingPredicate);
    }

    Aggregator<Map.Entry<Long, InvoiceKeywordIndexEntry>, BigDecimal> invoiceUsdTotaller =
        Aggregators.bigDecimalSum("amount");

    BigDecimal totalAmount = invoiceDataMap.aggregate(invoiceUsdTotaller, searchPredicate);

    List<Long> invoiceIds =
        searchInvoiceIds
            .parallelStream()
            .map(InvoiceKeywordIndexEntry::getInvoiceId)
            .collect(Collectors.toList());

    return InvoiceSearchResponse.builder().invoiceId(invoiceIds).totalAmount(totalAmount).build();
  }

  public Map<String, Set<String>> putInvoices(InvoiceSearchRequest invoiceSearchRequest) {
    Predicate<Long, InvoiceKeywordIndexEntry> p = buildPredicateInv(invoiceSearchRequest);
    return invoiceDataMap.aggregate(new InvoiceKeywordAggregator(), p);
  }


  public boolean permanentlyDeleteInvoices(Integer clientId) {
    KeywordSearchQuery<Long, InvoiceKeywordIndexEntry> trashQuery = new KeywordSearchQuery<>();
    trashQuery.addTerm("deletionStatusType", "Temporarily Deleted");
    trashQuery.addTerm("clientId", clientId);
    invoiceDataMap.removeAll(trashQuery.asPredicate());
    return true;
  }


  public Map<String, Set<String>> getInvoices() {
    return invoiceDataMap.aggregate(new InvoiceKeywordAggregator());

  }


  public boolean patchInvoices(List<Integer> invoiceId) {

    invoiceDataMap.putAll(
        invoiceSearchRepository.getInvoices(
            Optional.of(invoiceId))
            .stream()
            .map(e -> domainMapper.map(e, InvoiceKeywordIndexEntry.class))
            .collect(Collectors.toMap(InvoiceKeywordIndexEntry::getInvoiceId, e -> e)));
    return true;
  }


  private Predicate<Long, InvoiceKeywordIndexEntry> buildPredicateInv(
      InvoiceSearchRequest invoiceSearchRequest) {

    KeywordSearchQuery<Long, InvoiceKeywordIndexEntry> query;
    query = KeywordSearchQuery.createFromBeanObject(invoiceSearchRequest);

    return query.asPredicate();
  }


  // it's important for a comparator to be Serializable to use it in a multi-node
  // cluster
  private static class DescendingIdComparator
      implements Serializable, Comparator<Map.Entry<Long, InvoiceKeywordIndexEntry>> {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(
        Map.Entry<Long, InvoiceKeywordIndexEntry> o1,
        Map.Entry<Long, InvoiceKeywordIndexEntry> o2) {
      InvoiceKeywordIndexEntry s1 = o1.getValue();
      InvoiceKeywordIndexEntry s2 = o2.getValue();
      return s2.getId() - s1.getId();
    }
  }

  public boolean removeInvoices(Integer invoiceId) {
    KeywordSearchQuery<Long, InvoiceKeywordIndexEntry> trashQuery = new KeywordSearchQuery<>();
    trashQuery.addTerm("invoiceId", invoiceId);
    invoiceDataMap.removeAll(trashQuery.asPredicate());
    return true;
  }


  public boolean removeInvoice(Integer invoiceId) {
    KeywordSearchQuery<Long, InvoiceKeywordIndexEntry> trashQuery = new KeywordSearchQuery<>();
    trashQuery.addTerm("invoiceId", invoiceId);
    invoiceDataMap.removeAll(trashQuery.asPredicate());
    return true;
  }


  private static class InvoiceAmountDescendingSortComparator
      implements Serializable, Comparator<Map.Entry<Long, InvoiceKeywordIndexEntry>> {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(
        Map.Entry<Long, InvoiceKeywordIndexEntry> o1,
        Map.Entry<Long, InvoiceKeywordIndexEntry> o2) {

      Long invoiceId1 = o1.getValue().getInvoiceId();
      Long invoiceId2 = o2.getValue().getInvoiceId();

      BigDecimal amount1 = o1.getValue().getAmount();
      BigDecimal amount2 = o2.getValue().getAmount();

      int compare = amount2.compareTo(amount1);
      return compare == 0 ? invoiceId1.compareTo(invoiceId2) : compare;
    }
  }


  private static class InvoiceAmountAscendingSortComparator
      implements Serializable, Comparator<Map.Entry<Long, InvoiceKeywordIndexEntry>> {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(
        Map.Entry<Long, InvoiceKeywordIndexEntry> o1,
        Map.Entry<Long, InvoiceKeywordIndexEntry> o2) {

      Long invoiceId1 = o1.getValue().getInvoiceId();
      Long invoiceId2 = o2.getValue().getInvoiceId();

      BigDecimal amount1 = o1.getValue().getAmount();
      BigDecimal amount2 = o2.getValue().getAmount();

      int compare = amount1.compareTo(amount2);
      return compare == 0 ? invoiceId1.compareTo(invoiceId2) : compare;
    }
  }

}
