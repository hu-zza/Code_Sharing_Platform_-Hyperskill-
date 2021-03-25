package hu.zza.hyperskill.snippets;

import javax.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ErrorHandlingController {
  @ExceptionHandler(Exception.class)
  public ModelAndView handleError(HttpServletRequest request, Exception exception)
      throws Exception {

    // Rethrow the exceptions with @ResponseStatus annotation.
    if (AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class) != null) {
      throw exception;
    }

    ModelAndView mav = new ModelAndView();
    mav.addObject("exception", exception);
    mav.addObject("url", request.getRequestURL());
    mav.setViewName("error");
    return mav;
  }
}
