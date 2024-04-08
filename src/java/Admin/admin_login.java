package Admin;

import at.favre.lib.crypto.bcrypt.BCrypt;
import database.ConnectionPool;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/admin_login"})
public class admin_login extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name=request.getParameter("username");
        String pass=request.getParameter("password");
        
        if(pass==null || name==null || pass.isEmpty() || name.isEmpty()){
            response.getWriter().println("<h1>Name and password feids are empty</h1>");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Enter username password");
        }
        String sql="select * from admin where username=?";
        
        try(Connection connection=ConnectionPool.getConnection();
            PreparedStatement statement=connection.prepareStatement(sql)){
            
            statement.setString(1, name);
            ResultSet rs=statement.executeQuery();
            
            if(rs.next()==true){
                String Admin_password=rs.getString("password");
                int Admin_id=rs.getInt("admin_id");
                boolean cheakPass=BCrypt.verifyer().verify(pass.toCharArray(), Admin_password).verified;
                
                if(cheakPass){
                    
                    HttpSession session = request.getSession();
                    session.setAttribute("username", name);
                    session.setAttribute("id", Admin_id);
                    
                    
                    Cookie usercookie=new Cookie(name,name);
                    usercookie.setMaxAge(3000);
                    usercookie.setSecure(true);
                    usercookie.setHttpOnly(true);
                    response.addCookie(usercookie);
                    
                    response.getWriter().println("<h1>You are logged in</h1>");//add the url of the page need to redierct           
                }else{
                    response.getWriter().println("Incorect password");
                }
            }else{
                response.getWriter().println("<h1>No user found with the given username.</h1>");
            }
        }catch(Exception e){
            System.out.println("Error is :: "+e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Invaild creadentials");
        }
        
        
    }

    
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
