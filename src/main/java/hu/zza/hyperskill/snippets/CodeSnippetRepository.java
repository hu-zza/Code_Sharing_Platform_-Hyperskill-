package hu.zza.hyperskill.snippets;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CodeSnippetRepository extends CrudRepository<CodeSnippet, Long> {

  Optional<CodeSnippet> findByUuid(String uuid);

  default List<CodeSnippet> findLatest10() {
    return findTop10ByTimeLessThanEqualAndViewsLessThanEqualOrderByIdDesc(0L, 0L);
  }

  List<CodeSnippet> findTop10ByTimeLessThanEqualAndViewsLessThanEqualOrderByIdDesc(Long time,
      Long views);
}
