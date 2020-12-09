package hu.zza.hyperskill.snippets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;


@RestController
public class ApiController
{
    private final CodeSnippetRepository repository;
    
    @Autowired
    private ApiController(CodeSnippetRepository repository)
    {
        this.repository = repository;
    }
    
    public static final HttpHeaders RESPONSE_HEADERS = new HttpHeaders();
    
    static
    {
        RESPONSE_HEADERS.setContentType(MediaType.valueOf("application/json; charset=UTF-8"));
    }
    
    @PostMapping("/api/code/new")
    ResponseEntity<Map<String, String>> createCodeSnippet(@RequestBody CodeSnippet codeSnippet)
    {
        var snippet = repository.save(codeSnippet);
        
        return ResponseEntity
                       .ok()
                       .headers(RESPONSE_HEADERS)
                       .body(Map.of("id", String.valueOf(snippet.getUuid())));
    }
    
    @GetMapping("/api/code/{uuid}")
    ResponseEntity<CodeSnippet> getByIdAsJson(@PathVariable String uuid)
    {
        var optionalCodeSnippet = repository.findByUuid(uuid);
        
        if (optionalCodeSnippet.isPresent())
        {
            var codeSnippet = optionalCodeSnippet.get();
            
            if (codeSnippet.isAccessible())
            {
                codeSnippet.increaseViewCount();
                codeSnippet = repository.save(codeSnippet);
                
                return ResponseEntity
                               .ok()
                               .headers(RESPONSE_HEADERS)
                               .body(repository.save(codeSnippet));
            }
        }
        
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("This UUID (%s) is not exist.", uuid));
    }
    
    @GetMapping("/api/code/latest")
    ResponseEntity<List<CodeSnippet>> getLatest10AsJson()
    {
        List<CodeSnippet> snippetList = repository.findLatest10();
        
        snippetList.forEach(CodeSnippet::increaseViewCount);
        repository.saveAll(snippetList);
        
        return ResponseEntity
                       .ok()
                       .headers(RESPONSE_HEADERS)
                       .body(snippetList);
    }
}