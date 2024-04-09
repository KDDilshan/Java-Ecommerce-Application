    package product;

    import database.ConnectionPool;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.util.UUID;
    import static javax.faces.component.UIInput.isEmpty;
    import javax.servlet.ServletException;
    import javax.servlet.annotation.MultipartConfig;
    import javax.servlet.annotation.WebServlet;
    import javax.servlet.http.HttpServlet;
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    import javax.servlet.http.Part;

    @MultipartConfig
    @WebServlet(name = "product_create", urlPatterns = {"/product_create"})
    public class product_create extends HttpServlet {

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            String pname=request.getParameter("product_name");
            double price = ServletUtils.parseDouble(request.getParameter("price"), 0.0);
            int category = ServletUtils.parseInt(request.getParameter("categoryid"), 0);
            int subcategory = ServletUtils.parseInt(request.getParameter("subcategoryid"), 0);
            int quantity = ServletUtils.parseInt(request.getParameter("quantity_available"), 0);
            String size=request.getParameter("size");
            String discription=request.getParameter("discription");
            Part file=request.getPart("image");

            
            if(isEmpty(pname) || isEmpty(price) || isEmpty(category) || isEmpty(subcategory) || isEmpty(size) || isEmpty(discription) || isEmpty(quantity)){
                response.getWriter().println("<h1>Add all the values in the feilds</h1>");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
                
            }
//          file uploading
            String imagename=file.getSubmittedFileName();
                    
//          Generate uuid for given image
            String fileExtension = imagename.substring(imagename.lastIndexOf("."));
            String uniqueID = UUID.randomUUID().toString();
            String newImageName = uniqueID + fileExtension;
            
            
            String uploadPath = "C:\\Users\\HP\\Documents\\NetbeansNewProjects\\JavaEcommerce\\web\\uploads\\"+ newImageName;
            try {
                FileOutputStream fos = new FileOutputStream(uploadPath);
                InputStream is = file.getInputStream();

                byte[] data = new byte[is.available()];
                is.read(data);
                fos.write(data);
                fos.close();
            } catch(IOException e) {
                response.getWriter().println("<h1>error in adding images to uploads</h1>");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            

            String sql="Select * from admin where username=?";
            String sql2="insert into product(product_name,price,category_id,subcategory_id,size,description,quantity_available,admin_id)values(?,?,?,?,?,?,?,?)";
            String sql3="insert into image(product_id,image_url) values(?,?)";
            
//            Insert product into database
            try(Connection connection=ConnectionPool.getConnection()){
                
                String usernames=(String)request.getAttribute("username");
                
                if(usernames==null || usernames.isEmpty()){
                    response.getWriter().println("the admin name is empty");
                    response.setStatus(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
                }
                
                PreparedStatement statement1=connection.prepareStatement(sql);
                statement1.setString(1, usernames);
                ResultSet rs1 =statement1.executeQuery();
                
                    if(rs1.next()==true){
                        int adminNo=rs1.getInt("admin_id");

                        PreparedStatement statement2=connection.prepareStatement(sql2);
                        statement2.setString(1, pname);
                        statement2.setDouble(2, price);
                        statement2.setInt(3,category );
                        statement2.setInt(4,subcategory );
                        statement2.setString(5, size);
                        statement2.setString(6, discription);
                        statement2.setInt(7, quantity);
                        statement2.setInt(8, adminNo);
                        int rs2=statement2.executeUpdate();

                        if(rs2>0){
                            response.getWriter().println("<h1>product has added sucessfully to products</h1>");
                            
                            PreparedStatement statement3 = connection.prepareStatement("SELECT LAST_INSERT_ID()");
                            ResultSet set=statement3.executeQuery();
                            if(set.next()){
                                int pId=set.getInt(1);
                                PreparedStatement statement4=connection.prepareStatement(sql3);
                                statement4.setInt(1, pId);
                                statement4.setString(2, newImageName);
                                int result=statement4.executeUpdate();
                                
                                if(result>0){
                                    response.getWriter().println("<h1>product img added sucessfully</h1>");
                                }else{
                                    response.getWriter().println("<h1>product img added sucessfully </h1>");
                                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                }
                            }else{
                                response.getWriter().println("<h1>cant get last inserted id</h1>");
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            }
                            
                        }else{
                            response.getWriter().println("<h1>Cant add a peoduct</h1>");
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        }
                    }else{
                        response.getWriter().println("Error in taking admin id");
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                    
                }catch(Exception e){
                    response.getWriter().println("Error in product creation");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getLocalizedMessage());
                }

        }


        @Override
        public String getServletInfo() {
            return "Short description";
        }

    }
