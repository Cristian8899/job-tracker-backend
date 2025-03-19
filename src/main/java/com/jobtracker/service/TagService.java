package com.jobtracker.service;

import com.jobtracker.model.Tag;
import com.jobtracker.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    /** ✅ Validate and return only existing tags (bulk query for better performance) */
    public List<Tag> validateTags(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of(); // ✅ Return empty list if no tags provided
        }

        // Extract tag names from the list
        List<String> tagNames = tags.stream()
                .map(Tag::getName)
                .map(String::toLowerCase) // Normalize to lowercase
                .collect(Collectors.toList());

        // Query all matching tags in one go (case-insensitive search)
        List<Tag> existingTags = tagRepository.findByNameInIgnoreCase(tagNames);

        // Return only tags that exist in the database
        return existingTags;
    }


    /** ✅ Get up to 5 tag suggestions based on a filter (case-insensitive, regex-like behavior) */
    public List<Tag> getTagSuggestions(String filter) {
        if (filter == null || filter.trim().isEmpty()) {
            return tagRepository.findTop10ByOrderByNameAsc(); // ✅ Return top 5 sorted tags if no filter is provided
        }
        return tagRepository.findTop10ByNameContainingIgnoreCase(filter);
    }
}
