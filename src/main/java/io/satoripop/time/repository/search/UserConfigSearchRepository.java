package io.satoripop.time.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import io.satoripop.time.domain.UserConfig;
import io.satoripop.time.repository.UserConfigRepository;
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
 * Spring Data Elasticsearch repository for the {@link UserConfig} entity.
 */
public interface UserConfigSearchRepository extends ElasticsearchRepository<UserConfig, Long>, UserConfigSearchRepositoryInternal {}

interface UserConfigSearchRepositoryInternal {
    Stream<UserConfig> search(String query);

    Stream<UserConfig> search(Query query);

    void index(UserConfig entity);
}

class UserConfigSearchRepositoryInternalImpl implements UserConfigSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final UserConfigRepository repository;

    UserConfigSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, UserConfigRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<UserConfig> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<UserConfig> search(Query query) {
        return elasticsearchTemplate.search(query, UserConfig.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(UserConfig entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
