package hu.zza.hyperskill.snippets;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
  Optional<CodeSnippet> findByUuid(String uuid);
}
