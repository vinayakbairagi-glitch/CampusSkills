package com.campusskills.controller;

import com.campusskills.dto.SwapRequestAction;
import com.campusskills.dto.SwapRequestCreate;
import com.campusskills.entity.SwapRequest;
import com.campusskills.entity.User;
import com.campusskills.service.SwapService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/swap-requests")
public class SwapRequestController {

    private final SwapService swapService;

    public SwapRequestController(SwapService swapService) {
        this.swapService = swapService;
    }

    @PostMapping
    public ResponseEntity<SwapRequest> createRequest(@AuthenticationPrincipal User user,
                                                     @Valid @RequestBody SwapRequestCreate request) {
        return ResponseEntity.ok(swapService.createSwapRequest(user.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getMyRequests(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(swapService.getMySwapRequests(user.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SwapRequest> actionRequest(@AuthenticationPrincipal User user,
                                                     @PathVariable Long id,
                                                     @Valid @RequestBody SwapRequestAction action) {
        return ResponseEntity.ok(swapService.actionSwapRequest(user.getId(), id, action));
    }
}
