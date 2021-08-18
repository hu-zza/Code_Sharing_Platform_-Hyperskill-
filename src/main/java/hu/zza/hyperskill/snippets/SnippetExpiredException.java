package hu.zza.hyperskill.snippets;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "The snippet has expired.")
public class SnippetExpiredException extends ResponseStatusException {

  public SnippetExpiredException(HttpStatus status, String reason) {
    super(status, reason);
  }
}
