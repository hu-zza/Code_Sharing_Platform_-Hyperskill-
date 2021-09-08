package hu.zza.hyperskill.snippets.repository;

import hu.zza.hyperskill.snippets.model.CodeSnippet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeSnippetRepository extends CrudRepository<CodeSnippet, Long> {

  Optional<CodeSnippet> findByUuid(String uuid);

  default List<CodeSnippet> findLatest10() {
    return findTop10ByTimeLimitLessThanEqualAndViewLimitLessThanEqualOrderByIdDesc(0L, 0L);
  }

  List<CodeSnippet> findTop10ByTimeLimitLessThanEqualAndViewLimitLessThanEqualOrderByIdDesc(
      Long time, Long views);
}
