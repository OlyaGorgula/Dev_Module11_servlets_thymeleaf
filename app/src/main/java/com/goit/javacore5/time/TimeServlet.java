package com.goit.javacore5.time;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("C:\\Users\\User\\IdeaProjects\\Dev_Module11_servlets\\homework-dev-m11\\app\\templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        resp.setContentType("text/html; charset=utf-8");
        String timezone = parseTimeZone(req, resp);

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("time", getTime(timezone))
        );

        try(PrintWriter printWriter = resp.getWriter()) {
            engine.process("time", simpleContext, printWriter);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String parseTimeZone(HttpServletRequest req, HttpServletResponse resp){
        if(req.getParameterMap().containsKey("timezone")){
            String parseTimeZone = req.getParameter("timezone")
                    .replace("GMT","UTC")
                    .replace(" ", "+");
            resp.setHeader("Set-Cookie", "lastTimezone="+parseTimeZone);
            return parseTimeZone;
        }else {
            if (req.getHeader("Cookie") != null){
                return parseLastTimeZone(req.getHeader("Cookie"));
            }
        }
        return "UTC";
    }

    private String parseLastTimeZone(String cookie){
        if(cookie.contains("lastTimezone")){
            return cookie.replace("lastTimezone=","");
        }
        return "";
    }

    public String getTime(String id){

        TimeZone timezone = TimeZone.getTimeZone(id.replace("UTC","GMT"));
        Calendar calendar = Calendar.getInstance(timezone);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        formatter.setCalendar(calendar);
        formatter.setTimeZone(timezone);

        return formatter.format(calendar.getTime());
    }
}
