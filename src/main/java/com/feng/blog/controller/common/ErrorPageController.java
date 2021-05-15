package com.feng.blog.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

//提供错误配置的页面

/**
 * 错误页面的实现逻辑主要在 errorHtml() 方法，在实现代码中根据当前的错误码来转发到我们配置的错误页面，
 * 在这里我们只做了 3 个错误页面，分别是 400 、404 和 5xx 的错误页面，
 * 通过判断这些错误码将页面转发到 error 目录下对应的页面中，接下来我们在项目中新增这几个页面的 html 代码。
 */
@Controller
public class ErrorPageController  implements ErrorController {


    private static ErrorPageController errorPageController;

    @Autowired
    private ErrorAttributes errorAttributes;
    private final static  String ERROR_PATH="/error"; //错误执行的路径

    public ErrorPageController (ErrorAttributes errorAttributes){
        this.errorAttributes=errorAttributes;
    }


    public ErrorPageController(){
        if (errorPageController ==null){
            errorPageController=new ErrorPageController(errorAttributes);
        }
    }

    @RequestMapping(value = ERROR_PATH,produces = "text/html")
    public ModelAndView errorHtml(HttpServletRequest request){
        //在request中获取到他的状态
        HttpStatus status=getStatus(request);
        if (HttpStatus.BAD_REQUEST == status){
            return new ModelAndView("error/error_400");
        }else if (HttpStatus.NOT_FOUND == status){
            return new ModelAndView("error/error_404");
        } else {
            return  new ModelAndView("error/error_5xx");
        }

    }

    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public ResponseEntity<Map<String,Object>>error (HttpServletRequest request){

        Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
        HttpStatus status = getStatus(request);
        return  new ResponseEntity<Map<String ,Object>>(body,status);
    }


    @Override
    public String getErrorPath() {
        return null;
    }

    private boolean getTraceParameter (HttpServletRequest request){

        String parameter = request.getParameter("trace");
        if (parameter ==null){
            return  false;
        }
        return  !"false".equals(parameter.toLowerCase());
    }

    protected Map<String,Object> getErrorAttributes (HttpServletRequest request,boolean includeStactTrace){
       WebRequest webRequest = new ServletWebRequest(request);
       return this.errorAttributes.getErrorAttributes(webRequest,includeStactTrace);
    }

    private HttpStatus getStatus(HttpServletRequest request){
        Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");

        if (statusCode !=null){
            try{
                return HttpStatus.valueOf(statusCode);
            }catch (Exception ex){

            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
