package hu.zza.hyperskill.snippets.controller;

import hu.zza.hyperskill.snippets.model.Author;
import hu.zza.hyperskill.snippets.model.CodeSnippet;
import hu.zza.hyperskill.snippets.exception.SnippetNotFoundException;
import hu.zza.hyperskill.snippets.repository.AuthorRepository;
import hu.zza.hyperskill.snippets.repository.CodeSnippetRepository;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/code")
public class ApiController {

  public static final HttpHeaders RESPONSE_HEADERS = new HttpHeaders();

  static {
    RESPONSE_HEADERS.setContentType(MediaType.valueOf("application/json; charset=UTF-8"));
  }

  private final AuthorRepository authorRepository;
  private final CodeSnippetRepository codeSnippetRepository;

  @Autowired
  private ApiController(AuthorRepository authorRepo, CodeSnippetRepository codeRepo) {
    this.authorRepository = authorRepo;
    this.codeSnippetRepository = codeRepo;
  }

  @PostMapping("/new")
  ResponseEntity<Map<String, String>> createCodeSnippet(@RequestBody CodeSnippet codeSnippet) {
    var saved = codeSnippetRepository.save(codeSnippet);

    return ResponseEntity.ok(Map.of("id", String.valueOf(saved.getUuid())));
  }

  @PostMapping("/register")
  ResponseEntity<Map<String, String>> createAuthor(@RequestBody Author author) {
    var saved = authorRepository.save(author);

    return ResponseEntity.ok(Map.of("id", String.valueOf(saved.getUuid())));
  }

  @GetMapping("/{uuid}")
  ResponseEntity<CodeSnippet> getByIdAsJson(@PathVariable String uuid) {
    var optionalCodeSnippet = codeSnippetRepository.findByUuid(uuid);

    if (optionalCodeSnippet.isPresent()) {
      var codeSnippet = optionalCodeSnippet.get();

      if (codeSnippet.isAccessible()) {
        codeSnippet.increaseViewCount();
        codeSnippet = codeSnippetRepository.save(codeSnippet);

        return ResponseEntity.ok(codeSnippet);
      }
    }

    throw new SnippetNotFoundException(
        HttpStatus.NOT_FOUND, String.format("This UUID (%s) is not exist.", uuid));
  }

  @GetMapping("/latest")
  ResponseEntity<List<CodeSnippet>> getLatest10AsJson() {
    List<CodeSnippet> snippetList = codeSnippetRepository.findLatest10();

    snippetList.forEach(CodeSnippet::increaseViewCount);
    codeSnippetRepository.saveAll(snippetList);

    return ResponseEntity.ok(snippetList);
  }
}
