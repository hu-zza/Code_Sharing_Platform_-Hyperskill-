package hu.zza.hyperskill.snippets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Controller
public class ViewController
{
    private final CodeSnippetRepository repository;
    
    @Autowired
    private ViewController(CodeSnippetRepository repository)
    {
        this.repository = repository;
    }
    
    @GetMapping("/code/new")
    private String getSendingFormView()
    {
        return "createSnippet";
    }
    
    @GetMapping("/code/{uuid}")
    private String getByIdView(@PathVariable String uuid, Model model)
    {
        var optionalCodeSnippet = repository.findByUuid(uuid);
        
        if (optionalCodeSnippet.isPresent())
        {
            var codeSnippet = optionalCodeSnippet.get();
            
            if (codeSnippet.isAccessible())
            {
                codeSnippet.increaseViewCount();
                codeSnippet = repository.save(codeSnippet);
                
                model.addAttribute("snippet", codeSnippet);
                return "singleSnippet";
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("This UUID (%s) is not exist.", uuid));
    }
    
    @GetMapping("/code/latest")
    private String getLatest10View(Model model)
    {
        List<CodeSnippet> snippetList = repository.findLatest10();
        
        snippetList.forEach(CodeSnippet::increaseViewCount);
        repository.saveAll(snippetList);
        
        model.addAttribute("latestList", snippetList);
        return "latestSnippets";
    }
}
