package io.satoripop.time.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import io.satoripop.time.domain.WorkLog;
import io.satoripop.time.repository.WorkLogRepository;
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
 * Spring Data Elasticsearch repository for the {@link WorkLog} entity.
 */
public interface WorkLogSearchRepository extends ElasticsearchRepository<WorkLog, Long>, WorkLogSearchRepositoryInternal {}

interface WorkLogSearchRepositoryInternal {
    Stream<WorkLog> search(String query);

    Stream<WorkLog> search(Query query);

    void index(WorkLog entity);
}

class WorkLogSearchRepositoryInternalImpl implements WorkLogSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final WorkLogRepository repository;

    WorkLogSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, WorkLogRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<WorkLog> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<WorkLog> search(Query query) {
        return elasticsearchTemplate.search(query, WorkLog.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(WorkLog entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
