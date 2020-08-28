package com.hazelcast.jam.search.invoicesearch;

import com.hazelcast.jam.search.invoicesearch.domain.InvoiceSearchRequest;
import com.hazelcast.jam.search.invoicesearch.domain.InvoiceSearchResponse;
import com.hazelcast.jam.search.invoicesearch.domain.InvoiceUpdateRequest;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class InvoiceSearchController {

  private InvoiceSearchService invoiceSearchService;

  @Autowired
  public InvoiceSearchController(InvoiceSearchService invoiceSearchService) {
    this.invoiceSearchService = invoiceSearchService;
  }

  @RequestMapping(value = "/invoice", method = RequestMethod.GET)
  public ResponseEntity<Map<String, Set<String>>> getInvoice() {
    return ResponseEntity.ok(invoiceSearchService.getInvoices());
  }

  //  refresh index
  @RequestMapping(value = "/invoice", method = RequestMethod.DELETE)
  public ResponseEntity<Boolean> refreshSearchInvoice() {
    return ResponseEntity.ok(invoiceSearchService.refreshInvoices());
  }

  @RequestMapping(value = "/invoice", method = RequestMethod.POST)
  public ResponseEntity<InvoiceSearchResponse> searchInvoice(
      @RequestParam(value = "sortBy", required = false) final String sortBy,
      @RequestParam(value = "page", required = true) final Integer page,
      @RequestParam(value = "pageSize", required = true) final Integer pageSize,
      @RequestBody final InvoiceSearchRequest invoiceSearchRequest) {
    return ResponseEntity.ok(
        invoiceSearchService.searchInvoices(
            page, pageSize, Optional.ofNullable(sortBy), invoiceSearchRequest));
  }


  @RequestMapping(value = "/invoice", method = RequestMethod.PUT)
  public ResponseEntity<Map<String, Set<String>>> putInvoice(
      @RequestBody final InvoiceSearchRequest invoiceSearchRequest) {
    return ResponseEntity.ok(invoiceSearchService.putInvoices(invoiceSearchRequest));
  }


  @RequestMapping(value = "/invoice", method = RequestMethod.PATCH)
  public ResponseEntity<HttpStatus> patchInvoice(
      @RequestBody final InvoiceUpdateRequest invoiceUpdateRequest) {
    invoiceSearchService.patchInvoices(invoiceUpdateRequest.getInvoiceIds());
    return ResponseEntity.ok(HttpStatus.ACCEPTED);
  }

  //  Remove invoice from cache
  @RequestMapping(value = "/invoice/{invoiceId}", method = RequestMethod.DELETE)
  public ResponseEntity<Boolean> removeInvoiceFromCache(
      @PathVariable(value = "invoiceId") final Integer invoiceId) {
    return ResponseEntity.ok(invoiceSearchService.removeInvoices(invoiceId));
  }

}
