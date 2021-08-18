package hu.zza.hyperskill.snippets;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/code")
public class ViewController {
  private final AuthorRepository authorRepository;
  private final CodeSnippetRepository codeSnippetRepository;

  @Autowired
  private ViewController(AuthorRepository authorRepo, CodeSnippetRepository codeRepo) {
    this.authorRepository = authorRepo;
    this.codeSnippetRepository = codeRepo;
  }

  // Without argument this maps "/" and "/code" at the same time...
  @GetMapping
  private String getIndexView() {
    return "index";
  }

  @GetMapping("/new")
  private String getSendingFormView(Map<String, Object> model) {
    model.put("newSnippet", new CodeSnippet());
    return "createSnippet";
  }

  @PostMapping("/new")
  private String createCodeSnippet(CodeSnippet newSnippet) {
    var snippet = codeSnippetRepository.save(newSnippet);
    return String.format("redirect:/code/%s", snippet.getUuid());
  }

  @GetMapping("/{uuid}")
  private String getByIdView(@PathVariable String uuid, Map<String, Object> model) {
    var optionalCodeSnippet = codeSnippetRepository.findByUuid(uuid);

    if (optionalCodeSnippet.isPresent()) {
      var codeSnippet = optionalCodeSnippet.get();

      if (codeSnippet.isAccessible()) {
        codeSnippet.increaseViewCount();
        codeSnippet = codeSnippetRepository.save(codeSnippet);

        model.put("snippet", codeSnippet);
        model.put("author", authorRepository.getByUuid(codeSnippet.getUuid()));
        return "singleSnippet";
      }

      throw new SnippetExpiredException(
          HttpStatus.FORBIDDEN, String.format("The code snippet (%s) has expired.", uuid));
    }

    throw new SnippetNotFoundException(
        HttpStatus.NOT_FOUND, String.format("This UUID (%s) is not exist.", uuid));
  }

  @GetMapping("/latest")
  private String getLatest10View(Map<String, Object> model) {
    List<CodeSnippet> latestTenSnippets = codeSnippetRepository.findLatest10();
    List<Author> latestTenAuthors =
        latestTenSnippets.stream()
            .map(CodeSnippet::getAuthorUuid)
            .map(authorRepository::getByUuid)
            .collect(Collectors.toList());

    latestTenSnippets.forEach(CodeSnippet::increaseViewCount);
    codeSnippetRepository.saveAll(latestTenSnippets);

    model.put("latestTenSnippets", latestTenSnippets);
    model.put("latestTenAuthors", latestTenAuthors);
    return "latestSnippets";
  }

  @GetMapping("/trending")
  private String getStatisticsPage(Map<String, Object> model) {
    return "trendingAndInsights";
  }
}
