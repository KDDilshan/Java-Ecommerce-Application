package product;

import database.ConnectionPool;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static javax.faces.component.UIInput.isEmpty;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "product_update", urlPatterns = {"/product_update"})
public class product_update extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
//        title description price qunqntiiy pcture
          int productId = ServletUtils.parseInt(request.getParameter("productId"),0);
          String title=request.getParameter("productname");
          String discription=request.getParameter("discription");
          double price=ServletUtils.parseDouble(request.getParameter("price"), 0);
          int quantity=ServletUtils.parseInt(request.getParameter("quantity_available"), 0);
          
           if (title == null || discription == null || title.isEmpty() || discription.isEmpty()) {
                response.getWriter().println("<h1>Add all the values in the fields</h1>");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

          if(productId==0){
              
             response.getWriter().println("<h1>PID is worng</h1>"+productId+" "+title+" "+discription+" "+price+" "+quantity);
             response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             return;
          }
          
          String sql="UPDATE product\n" +
                    "SET product_name = ?, description = ?, price = ?, quantity_available = ?\n" +
                    "WHERE product_id = ?";
          
          try(Connection connection=ConnectionPool.getConnection()){
              
              if (connection == null) {
                    response.getWriter().println("Error: Unable to get database connection");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
              
              PreparedStatement statement=connection.prepareStatement(sql);
              statement.setString(1, title);
              statement.setString(2, discription);
              statement.setDouble(3, price);
              statement.setInt(4, quantity);
              statement.setInt(5, productId);
              int res=statement.executeUpdate();
              
              if(res>0){
                  response.getWriter().println("<h1>updated the product sucessfully</h1>");
                  response.setStatus(HttpServletResponse.SC_ACCEPTED);
              }else{
                  response.getWriter().println("<h1> not updated product sucessfully</h1>");
                  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  return;
              }
          }catch(Exception e){
              e.printStackTrace();
              response.getWriter().println("Error in product update");
              response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getLocalizedMessage());
          }
       
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
