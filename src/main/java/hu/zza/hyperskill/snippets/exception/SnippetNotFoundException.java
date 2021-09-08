package hu.zza.hyperskill.snippets.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such code snippet")
public class SnippetNotFoundException extends ResponseStatusException {

  public SnippetNotFoundException(HttpStatus status, String reason) {
    super(status, reason);
  }
}
