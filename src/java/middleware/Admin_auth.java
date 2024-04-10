package middleware;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns={"/product_create","/product_update"})
public class Admin_auth implements Filter{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httprequest=(HttpServletRequest) request;
        HttpServletResponse httpresponse=(HttpServletResponse) response;
        HttpSession session=httprequest.getSession(false);
        
        if(session != null){
            String username=(String) session.getAttribute("username");
            if(username != null){
               request.setAttribute("username",username ); 
               chain.doFilter(request, response);
            }else{
                httpresponse.sendRedirect("admin_login.java");
            } 
        }else{
            httpresponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpresponse.getWriter().println("<h1>logged in to access this page</h1>");    
        } 
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }
    @Override
    public void destroy() {

    }
    
}
