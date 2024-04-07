package Admin;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import database.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.http.HttpSession;


@WebServlet("/CreateAdmin")
public class admin extends HttpServlet {

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String name = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phoneNo = request.getParameter("phoneno");

        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        String message;
        HttpSession session = request.getSession();

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO admin(username, password, email, phone_no) VALUES (?, ?, ?, ?)")) {

            statement.setString(1, name);
            statement.setString(2, hashedPassword);
            statement.setString(3, email);
            statement.setString(4, phoneNo);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                message = "Admin registered successfully!";
                session.setAttribute("successMessage", message);
                response.getWriter().println("<h1>Registration Successful</h1>");
            } else {
                message = "Admin registration failed.";
                session.setAttribute("errorMessage", message);
                response.sendRedirect("admin.jsp");
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred. Please try again later.");
        }
    }

   
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
