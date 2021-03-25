package hu.zza.hyperskill.snippets;

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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/code")
public class ApiController {

  public static final HttpHeaders RESPONSE_HEADERS = new HttpHeaders();

  static {
    RESPONSE_HEADERS.setContentType(MediaType.valueOf("application/json; charset=UTF-8"));
  }

  private final CodeSnippetRepository repository;

  @Autowired
  private ApiController(CodeSnippetRepository repository) {
    this.repository = repository;
  }

  @PostMapping("/new")
  ResponseEntity<Map<String, String>> createCodeSnippet(@RequestBody CodeSnippet codeSnippet) {
    var snippet = repository.save(codeSnippet);

    return ResponseEntity.ok(Map.of("id", String.valueOf(snippet.getUuid())));
  }

  @GetMapping("/{uuid}")
  ResponseEntity<CodeSnippet> getByIdAsJson(@PathVariable String uuid) {
    var optionalCodeSnippet = repository.findByUuid(uuid);

    if (optionalCodeSnippet.isPresent()) {
      var codeSnippet = optionalCodeSnippet.get();

      if (codeSnippet.isAccessible()) {
        codeSnippet.increaseViewCount();
        codeSnippet = repository.save(codeSnippet);

        return ResponseEntity.ok(codeSnippet);
      }
    }

    throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, String.format("This UUID (%s) is not exist.", uuid));
  }

  @GetMapping("/latest")
  ResponseEntity<List<CodeSnippet>> getLatest10AsJson() {
    List<CodeSnippet> snippetList = repository.findLatest10();

    snippetList.forEach(CodeSnippet::increaseViewCount);
    repository.saveAll(snippetList);

    return ResponseEntity.ok(snippetList);
  }
}
