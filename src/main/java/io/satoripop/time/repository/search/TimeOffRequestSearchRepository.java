package io.satoripop.time.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import io.satoripop.time.domain.TimeOffRequest;
import io.satoripop.time.repository.TimeOffRequestRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link TimeOffRequest} entity.
 */
public interface TimeOffRequestSearchRepository
    extends ElasticsearchRepository<TimeOffRequest, Long>, TimeOffRequestSearchRepositoryInternal {}

interface TimeOffRequestSearchRepositoryInternal {
    Stream<TimeOffRequest> search(String query);

    Stream<TimeOffRequest> search(Query query);

    void index(TimeOffRequest entity);
}

class TimeOffRequestSearchRepositoryInternalImpl implements TimeOffRequestSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TimeOffRequestRepository repository;

    TimeOffRequestSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, TimeOffRequestRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<TimeOffRequest> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<TimeOffRequest> search(Query query) {
        return elasticsearchTemplate.search(query, TimeOffRequest.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(TimeOffRequest entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
