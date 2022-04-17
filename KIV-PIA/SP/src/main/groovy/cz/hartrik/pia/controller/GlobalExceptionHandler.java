package cz.hartrik.pia.controller;

import cz.hartrik.pia.WrongInputException;
import javax.servlet.ServletException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Contains global exception handlers.
 *
 * @version 2018-11-25
 * @author Patrik Harag
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private String getFallBackView() {
        return "forward:/";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handle404() {
        ModelAndView model = new ModelAndView(getFallBackView());
        model.getModel().put("error", "Page not found!");
        return model;
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            ServletRequestBindingException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            TypeMismatchException.class,
            WrongInputException.class,  // custom
            AccessDeniedException.class,  // custom
            BindException.class,  // spring - request params validation
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handle400(Exception ex)   {
        ModelAndView model = new ModelAndView(getFallBackView());
        model.getModel().put("error", "Bad request: " + ex.getMessage());
        return model;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ModelAndView handle405()   {
        ModelAndView model = new ModelAndView(getFallBackView());
        model.getModel().put("error", "Method not allowed!");
        return model;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ModelAndView handle415()   {
        ModelAndView model = new ModelAndView(getFallBackView());
        model.getModel().put("error", "Unsupported media type!");
        return model;
    }

    @ExceptionHandler({
            RuntimeException.class,
            ServletException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleUnknownException(RuntimeException e) {
        // server errors :(
        e.printStackTrace(System.err);

        ModelAndView model = new ModelAndView(getFallBackView());
        model.getModel().put("error", "Server error: " + e.getMessage());
        return model;
    }

}
