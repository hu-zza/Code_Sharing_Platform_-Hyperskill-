package hu.zza.hyperskill.snippets;

import java.util.List;
import java.util.Map;
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

  private final CodeSnippetRepository repository;

  @Autowired
  private ViewController(CodeSnippetRepository repository) {
    this.repository = repository;
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
    var snippet = repository.save(newSnippet);
    return String.format("redirect:/code/%s", snippet.getUuid());
  }

  @GetMapping("/{uuid}")
  private String getByIdView(@PathVariable String uuid, Map<String, Object> model) {
    var optionalCodeSnippet = repository.findByUuid(uuid);

    if (optionalCodeSnippet.isPresent()) {
      var codeSnippet = optionalCodeSnippet.get();

      if (codeSnippet.isAccessible()) {
        codeSnippet.increaseViewCount();
        codeSnippet = repository.save(codeSnippet);

        model.put("snippet", codeSnippet);
        return "singleSnippet";
      }
    }

    throw new SnippetNotFoundException(
        HttpStatus.NOT_FOUND, String.format("This UUID (%s) is not exist.", uuid));
  }

  @GetMapping("/latest")
  private String getLatest10View(Map<String, Object> model) {
    List<CodeSnippet> latestTenSnippets = repository.findLatest10();

    latestTenSnippets.forEach(CodeSnippet::increaseViewCount);
    repository.saveAll(latestTenSnippets);

    model.put("latestTenSnippets", latestTenSnippets);
    return "latestSnippets";
  }

  @GetMapping("/trending")
  private String getStatisticsPage(Map<String, Object> model) {
    return "trendingAndInsights";
  }
}
