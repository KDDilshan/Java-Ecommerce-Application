package product;

import database.ConnectionPool;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "product_delete", urlPatterns = {"/product_delete"})
public class product_delete extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
       String username=(String) request.getAttribute("username");
       
       String sql="select * from admin where username=?";
       String sql2="delete from product where admin_id=?";
       
       try(Connection connection=ConnectionPool.getConnection()){
           
           PreparedStatement statement1=connection.prepareStatement(sql);
           statement1.setString(1, username);
           ResultSet res=statement1.executeQuery();
           
           if(res.next()){
               int adminid=res.getInt("admin_id");
               
               PreparedStatement statement2=connection.prepareStatement(sql2);
               statement2.setInt(1, adminid);
               statement2.executeUpdate();
               
             response.getWriter().println("<h1>Product deleted successfully</h1>");
             response.setStatus(HttpServletResponse.SC_OK);
           }else{
               response.getWriter().println("admin not selected ");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
           }
           
       }catch(Exception e){
           response.getWriter().println("<h1>Error happend:"+e.getMessage()+"</h1>");
           response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
       }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}

