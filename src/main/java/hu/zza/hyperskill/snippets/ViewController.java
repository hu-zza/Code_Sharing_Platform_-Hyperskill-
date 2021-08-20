package hu.zza.hyperskill.snippets;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ViewController {
  private final AuthorRepository authorRepository;
  private final CodeSnippetRepository codeSnippetRepository;

  @Autowired
  private ViewController(AuthorRepository authorRepo, CodeSnippetRepository codeRepo) {
    this.authorRepository = authorRepo;
    this.codeSnippetRepository = codeRepo;
  }

  @GetMapping
  private String getIndexView() {
    return "index";
  }

  @GetMapping("/register")
  private String startRegistration(Map<String, Object> model) {
    model.put("newAuthor", new Author());
    return "registerAuthor";
  }

  @PostMapping("/register")
  private String registerAuthor(Author newAuthor) {
    boolean result = false;
    try {
      var author = authorRepository.save(newAuthor);
      if (0 < author.getId()) {
        result = true;
      }
    } catch (Exception ignored) {}

    return String.format("redirect:/?reg=%b", result);
  }

  @GetMapping("/login")
  private String startLogin(Map<String, Object> model) {
    model.put("credentials", new BaseCredentials());
    return "loginAuthor";
  }

  @PostMapping("/login")
  private String checkLoginCredentials(BaseCredentials credentials, HttpServletResponse response) {
    boolean result = false;

    var optAuthor = authorRepository.findByEmail(credentials.getEmail());
    if (optAuthor.isPresent()) {
      var author = optAuthor.get();

      if (author.checkPassword(credentials.getPassword())) {
        result = true;

        var cookie = new Cookie("uuid", author.getUuid());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(credentials.isRemember() ? 1_166_000 : 0);
        response.addCookie(cookie);
      }
    }

    return String.format("redirect:/?auth=%b", result);
  }

  @GetMapping("/code/new")
  private String getSendingFormView(Map<String, Object> model) {
    model.put("newSnippet", new CodeSnippet());
    return "createSnippet";
  }

  @PostMapping("/code/new")
  private String createCodeSnippet(CodeSnippet newSnippet) {
    var snippet = codeSnippetRepository.save(newSnippet);
    return String.format("redirect:/code/%s", snippet.getUuid());
  }

  @GetMapping("/code/{uuid}")
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

  @GetMapping("/code/latest")
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

  @GetMapping("/code/trending")
  private String getStatisticsPage(Map<String, Object> model) {
    return "trendingAndInsights";
  }
}
