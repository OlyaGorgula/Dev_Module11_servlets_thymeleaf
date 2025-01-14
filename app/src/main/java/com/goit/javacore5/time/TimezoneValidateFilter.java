package com.goit.javacore5.time;

import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TimeZone;

import static java.util.TimeZone.getTimeZone;

@WebFilter(value = "/time/*")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req,
                            HttpServletResponse res,
                            FilterChain chain) {
        if(req.getParameterMap().containsKey("timezone")){
            String id = req.getParameter("timezone").replace(" ", "+");

            if (id.equals("UTC") || id.equals("GMT")) {chainDoFilter(req, res,chain); return;}

            String idGMT = id.replace("UTC","GMT");
            TimeZone timeZoneGMT = getTimeZone(idGMT);
            if (timeZoneGMT.getID().equals("GMT")){
                errorIdTimeZone(res);
            }else{
                chainDoFilter(req, res,chain);
            }

        }else {
            chainDoFilter(req, res,chain);
        }
    }

    private void errorIdTimeZone(HttpServletResponse res) {
        res.setStatus(400);
        res.setContentType("text/html; charset=utf-8");
        try(PrintWriter printWriter = res.getWriter();) {
            printWriter.write("Invalid timezone");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void chainDoFilter(HttpServletRequest req,
                               HttpServletResponse res,
                               FilterChain chain){
        try {
            chain.doFilter(req, res);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
