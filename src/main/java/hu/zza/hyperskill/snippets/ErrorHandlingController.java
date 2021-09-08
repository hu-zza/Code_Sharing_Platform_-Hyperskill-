package hu.zza.hyperskill.snippets;

import javax.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandlingController {
  @ExceptionHandler(Exception.class)
  public String handleError(HttpServletRequest request, Exception exception, Model model)
      throws Exception {

    // Rethrow the exceptions with @ResponseStatus annotation.
    if (AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class) != null) {
      throw exception;
    }

    model.addAttribute("exception", exception);
    model.addAttribute("url", request.getRequestURL());
    return "error";
  }
}
