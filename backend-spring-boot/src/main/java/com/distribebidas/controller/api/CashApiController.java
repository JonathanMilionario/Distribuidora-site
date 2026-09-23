package com.distribebidas.controller.api;

import com.distribebidas.model.dto.DailyCashReportDto;
import com.distribebidas.service.DailyCashService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/cash")
@CrossOrigin(origins = "*")
public class CashApiController {

    private final DailyCashService dailyCashService;

    public CashApiController(DailyCashService dailyCashService) {
        this.dailyCashService = dailyCashService;
    }

    @GetMapping("/today")
    public ResponseEntity<DailyCashReportDto> getTodayReport() {
        return ResponseEntity.ok(dailyCashService.getTodayReport());
    }

    @PostMapping("/count")
    public ResponseEntity<DailyCashReportDto> countCash(@RequestBody Map<String, Object> payload) {
        Object countedObj = payload.get("countedCash");
        BigDecimal counted = countedObj != null ? new BigDecimal(countedObj.toString()) : BigDecimal.ZERO;
        String notes = (String) payload.getOrDefault("notes", "");
        dailyCashService.countCash(counted, notes);
        return ResponseEntity.ok(dailyCashService.getTodayReport());
    }

    @PostMapping("/supply")
    public ResponseEntity<DailyCashReportDto> addSupply(@RequestBody Map<String, Object> payload) {
        Object amountObj = payload.get("amount");
        BigDecimal amount = amountObj != null ? new BigDecimal(amountObj.toString()) : BigDecimal.ZERO;
        String reason = (String) payload.getOrDefault("reason", "");
        dailyCashService.addSupply(amount, reason);
        return ResponseEntity.ok(dailyCashService.getTodayReport());
    }

    @PostMapping("/bleeding")
    public ResponseEntity<DailyCashReportDto> addBleeding(@RequestBody Map<String, Object> payload) {
        Object amountObj = payload.get("amount");
        BigDecimal amount = amountObj != null ? new BigDecimal(amountObj.toString()) : BigDecimal.ZERO;
        String reason = (String) payload.getOrDefault("reason", "");
        dailyCashService.addBleeding(amount, reason);
        return ResponseEntity.ok(dailyCashService.getTodayReport());
    }

    @PostMapping("/close")
    public ResponseEntity<DailyCashReportDto> closeRegister(@RequestBody Map<String, Object> payload) {
        Object countedObj = payload.get("countedCash");
        BigDecimal counted = countedObj != null ? new BigDecimal(countedObj.toString()) : null;
        String notes = (String) payload.getOrDefault("notes", "");
        dailyCashService.closeRegister(counted, notes);
        return ResponseEntity.ok(dailyCashService.getTodayReport());
    }

    @PostMapping("/reopen")
    public ResponseEntity<DailyCashReportDto> reopenRegister() {
        dailyCashService.reopenRegister();
        return ResponseEntity.ok(dailyCashService.getTodayReport());
    }
}
