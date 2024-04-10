    package product;

    import database.ConnectionPool;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.Collection;
    import java.util.List;
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
            
            
            
            if(isEmpty(pname) || isEmpty(price) || isEmpty(category) || isEmpty(subcategory) || isEmpty(size) || isEmpty(discription) || isEmpty(quantity)){
                response.getWriter().println("<h1>Add all the values in the feilds</h1>");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return; 
            }
            
//           Fle uploading
//          get all the uploaded files
            Collection<Part> parts = request.getParts();
            //            list to store imag urls
            List<String> imageUrls = new ArrayList<>();
       
            if (parts == null) {
                response.getWriter().println("Error: No parts found in the request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                System.out.println("Error: No parts found in the request");
            } else if (parts.isEmpty()) {
                response.getWriter().println("No file uploaded");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                System.out.println("No file uploaded");
            
            }else{
            for(Part part: parts){
                 if (part == null) {
                    continue; // Skip null parts
                }
                String contentType = part.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    continue; // Skip parts that are not images or have no content type
                }
                if(!contentType.startsWith("image/")){
                    continue;
                }
//                generate uuid for file names
                System.out.println("ok4");
                String imagename=part.getSubmittedFileName();
                if (imagename == null || imagename.isEmpty()) {
                    continue; // Skip parts without a file name
                }
                String fileExtension = imagename.substring(imagename.lastIndexOf("."));
                String uniqueID = UUID.randomUUID().toString();
                String newImageName = uniqueID + fileExtension;
                
                //file uploading to local dir
                String uploadPath = "C:\\Users\\HP\\Documents\\NetbeansNewProjects\\JavaEcommerce\\web\\uploads\\"+ newImageName;
                try {
                    FileOutputStream fos = new FileOutputStream(uploadPath);
                    InputStream is = part.getInputStream();

                    byte[] data = new byte[is.available()];
                    is.read(data);
                    fos.write(data);
                    fos.close();
                } catch(IOException e) {
                    response.getWriter().println("<h1>error in adding image:"+e.getMessage()+"</h1>");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                
                imageUrls.add(newImageName);
                
            }
            }
            
            String sql="Select * from admin where username=?";
            String sql2="insert into product(product_name,price,category_id,subcategory_id,size,description,quantity_available,admin_id)values(?,?,?,?,?,?,?,?)";
            String sql3="insert into image(product_id,image_url) values(?,?)";
            
//            Insert product into database
            try(Connection connection=ConnectionPool.getConnection()){
                
                 if (connection == null) {
                    response.getWriter().println("Error: Unable to get database connection");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
                
                String usernames=(String)request.getAttribute("username");
                
                if(usernames==null || usernames.isEmpty()){
                    response.getWriter().println("the admin name is empty");
                    response.setStatus(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
                    return;
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
                                
                                PreparedStatement imgstatement4 = connection.prepareStatement(sql3);
                                if (imageUrls != null && !imageUrls.isEmpty()) {
                                    for (String imageUrl : imageUrls) {
                                        imgstatement4.setInt(1, pId);
                                        if (imageUrl != null || !imageUrl.isEmpty()) {
                                            imgstatement4.setString(2, imageUrl);
                                            imgstatement4.addBatch();
                                        }
                                    }
                                    int[] result = imgstatement4.executeBatch();
                                    
                                    if(result.length>0 && result!=null){
                                        response.getWriter().println("its added");
                                    }
                                } else {
                                    
                                    response.getWriter().println("<h1>No image URLs provided</h1>");
                                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
                    
                }catch(SQLException e ){
                    e.printStackTrace();
                    response.getWriter().println("Error in product creation");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getLocalizedMessage());
                }

        }


        @Override
        public String getServletInfo() {
            
            return "Short description";
        }

    }



