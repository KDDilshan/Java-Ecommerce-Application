package product;

import database.ConnectionPool;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;


@WebServlet(name = "product_view", urlPatterns = {"/product_view"})
public class product_view extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username=(String)request.getAttribute("username");
        
        String sql1="select * from admin where username=?";
        String sql2="select * from product where admin_id=?";
        
        
        try(Connection connection=ConnectionPool.getConnection()){
            
            if(connection==null){
                response.getWriter().println("Error: Unable to get database connection");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
           
            PreparedStatement statement1=connection.prepareStatement(sql1);
            statement1.setString(1, username);
            ResultSet res=statement1.executeQuery();
            
            if(res.next()){
               
                int adminId=res.getInt("admin_id");
                 
                PreparedStatement statement2=connection.prepareStatement(sql2);
                statement2.setInt(1, adminId);
                ResultSet res2=statement2.executeQuery();
                
                JSONArray productsArray=new JSONArray();
                
                try{
                    while(res2.next()){
                        JSONObject productobject=new JSONObject();
                        
                        int productId=res2.getInt("product_id");
                        String productname=res2.getString("product_name");
                        double price=res2.getDouble("price");
                        String discription=res2.getString("description");
                        String size=res2.getString("size");
                        
                        productobject.put("productId", productId);
                        productobject.put("productname", productname);
                        productobject.put("price", price);
                        productobject.put("discription", discription);
                        productobject.put("size", size);
                        
                        productsArray.put(productobject.toString());
                    }
                    response.setContentType("appplication/json");
                    response.getWriter().println(productsArray.toList());
                }catch(SQLException e){
                    response.getWriter().println("Error restriving products: "+e.getMessage());
                }
                
            }else{
                    response.getWriter().println("Error in the admin id getting");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            
            
        }catch(Exception e){
            response.getWriter().println("<h1>Error in getiing products"+e.getMessage()+"</h1>");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        
    }




    @Override
    public String getServletInfo() {
        return "Short description";
    }
    

}
