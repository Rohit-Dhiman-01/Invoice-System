package com.invoice.system.controller;

import com.invoice.system.dto.ApproveDto;
import com.invoice.system.dto.QuoteDto;
import com.invoice.system.dto.QuoteResponse;
import com.invoice.system.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/customer/{customerId}/quotes")
public class QuoteController {
    @Autowired
    private QuoteService quoteService;

    @PostMapping
    public ResponseEntity<QuoteResponse> createQuote(
            @RequestBody QuoteDto quoteDto, @PathVariable Long customerId) {
        return new ResponseEntity<>(quoteService.createQuote(quoteDto, customerId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<QuoteResponse>> getAllQuote(
            @PathVariable(value = "customerId", required = false) Long customerId) {
        return new ResponseEntity<>(quoteService.getAllQuote(customerId), HttpStatus.OK);
    }

    @GetMapping("{quoteId}")
    public ResponseEntity<QuoteResponse> getQuoteWithID(
            @PathVariable Long quoteId, @PathVariable Long customerId) {
        return new ResponseEntity<>(quoteService.getQuoteWithID(quoteId, customerId), HttpStatus.OK);
    }

    @PutMapping("{quoteId}")
    public ResponseEntity<QuoteResponse> updateQuoteWithID(
            @PathVariable Long quoteId, @PathVariable Long customerId, @RequestBody QuoteDto quoteDto) {
        return new ResponseEntity<>(
                quoteService.updateQuoteWithID(quoteId, customerId, quoteDto), HttpStatus.OK);
    }

    @DeleteMapping("{quoteId}")
    public ResponseEntity<Void> deleteQuoteWithID(
            @PathVariable Long quoteId, @PathVariable Long customerId) {
        quoteService.deleteQuoteWithID(quoteId, customerId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("{quoteId}/status")
    public ResponseEntity<Void> approveQuote(
            @PathVariable Long quoteId,
            @PathVariable Long customerId,
            @RequestBody ApproveDto approveDto) {
        quoteService.approveQuote(quoteId, customerId, approveDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("{quoteId}/pdf")
    public ResponseEntity<InputStreamResource> generateQuotePDF(
            @PathVariable Long quoteId,
            @PathVariable Long customerId) {
        ByteArrayInputStream pdfStream = quoteService.generateQuotePdf(quoteId, customerId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=Quote.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}
