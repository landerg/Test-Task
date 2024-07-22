package com.testtask.test;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> docCollection = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
        }
        document.setCreated(Instant.now());
        docCollection.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {

        return docCollection.values().stream()
                .filter(doc -> matchesSearchRequest(doc, request))
                .collect(Collectors.toList());
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(docCollection.get(id));
    }

    private boolean matchesSearchRequest(Document doc, SearchRequest request){
        boolean matches = true;
        if (request.getTitlePrefixes() != null) {
            matches = request.getTitlePrefixes().stream().anyMatch(prefix -> doc.getTitle().startsWith(prefix));
        }
        if (matches && request.getContainsContents() != null) {
            matches = request.getContainsContents().stream().anyMatch(content -> doc.getContent().contains(content));
        }
        if (matches && request.getAuthorIds() != null) {
            matches = request.getAuthorIds().contains(doc.getAuthor().getId());
        }
        if (matches && request.getCreatedFrom() != null) {
            matches = !doc.getCreated().isBefore(request.getCreatedFrom());
        }
        if (matches && request.getCreatedTo() != null) {
            matches = !doc.getCreated().isAfter(request.getCreatedTo());
        }
        return matches;
    }

    public void allDataOutput() {
        docCollection.values().forEach(System.out::println);
    }

    /**
     * This method populates the docCollection with randomly generated data.
     *
     * @param count - the number of documents to generate
     */
    public void randomDataGenerator(int count) {
        for (int i = 0; i < count; i++) {
            Author author = Author.builder()
                    .id(UUID.randomUUID().toString())
                    .name("Author " + i)
                    .build();

            Document document = Document.builder()
                    .id(UUID.randomUUID().toString())
                    .title("Title " + i)
                    .content("This is the content of document " + i)
                    .author(author)
                    .created(Instant.now().minusSeconds(ThreadLocalRandom.current().nextInt(0, 1000000)))
                    .build();

            docCollection.put(document.getId(), document);
        }
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
