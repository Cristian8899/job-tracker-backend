package com.jobtracker.controller;

import com.jobtracker.model.Tag;
import com.jobtracker.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<Tag>> getTagSuggestions(@RequestParam(required = false) String filter) {
        List<Tag> suggestedTags = tagService.getTagSuggestions(filter);
        return ResponseEntity.ok(suggestedTags);
    }
}
