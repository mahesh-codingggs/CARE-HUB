package com.carehub.carehub.controller;

import com.carehub.carehub.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @GetMapping("/stock-predictions")
    public List<Map<String, Object>> stockPredictions() {
        return aiService.stockPredictions();
    }

    @GetMapping("/reorder-recommendations")
    public List<Map<String, Object>> reorderRecommendations() {
        return aiService.reorderRecommendations();
    }

    @GetMapping("/fast-moving")
    public List<Map<String, Object>> fastMoving(@RequestParam(defaultValue = "10") int limit) {
        return aiService.fastMovingMedicines(limit);
    }

    @GetMapping("/slow-moving")
    public List<Map<String, Object>> slowMoving() {
        return aiService.slowMovingMedicines();
    }
}
