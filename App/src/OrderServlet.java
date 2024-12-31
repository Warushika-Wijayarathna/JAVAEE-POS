import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/order")
public class OrderServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        System.out.println("");
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MedplusCarePharmacy", "root", "Ijse@1234");

            // Execute the query to fetch customers
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM `Order`;").executeQuery();

            // Build the JSON response using StringBuilder
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");

            boolean first = true;
            while (resultSet.next()) {
                if (!first) {
                    jsonBuilder.append(",");
                }

                // Format each customer record as a JSON object
                String customerJson = String.format(
                        "{\"id\":\"%s\",\"cust_id\":\"%s\",\"user_id\":\"%s\",\"total\":\"%s\",\"date\":\"%s\"}",
                        resultSet.getString("o_id"),
                        resultSet.getString("cust_id"),
                        resultSet.getString("user_id"),
                        resultSet.getString("total"),
                        resultSet.getString("date")
                );

                jsonBuilder.append(customerJson);
                first = false;
            }

            jsonBuilder.append("]");

            // Close the resources
            resultSet.close();
            connection.close();

            // Write the JSON response
            resp.getWriter().write(jsonBuilder.toString());
        } catch (ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"JDBC Driver not found.\"}");

            e.printStackTrace();
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void doPost(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Connection connection = null;
        PreparedStatement orderStatement = null;
        PreparedStatement lastIdStatement = null;
        PreparedStatement orderItemStatement = null;

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to the database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MedplusCarePharmacy", "root", "Ijse@1234");

            // Start transaction: disable auto-commit for manual transaction control
            connection.setAutoCommit(false);

            // Insert new order
            String orderQuery = "INSERT INTO `Order` (cust_id, user_id, total, date) VALUES (?, ?, ?, ?)";
            orderStatement = connection.prepareStatement(orderQuery);
            orderStatement.setString(1, request.getParameter("cust_id"));
            orderStatement.setString(2, request.getParameter("user_id"));
            orderStatement.setString(3, request.getParameter("total"));
            orderStatement.setString(4, request.getParameter("date"));
            orderStatement.executeUpdate();

            // Get last inserted order ID
            lastIdStatement = connection.prepareStatement("SELECT LAST_INSERT_ID() as last_id");
            ResultSet resultSet = lastIdStatement.executeQuery();
            resultSet.next();
            String lastId = resultSet.getString("last_id");

            // Insert order items
            String[] items = request.getParameter("items").split(",");
            for (String item : items) {
                String[] parts = item.split(":");
                String itemQuery = "INSERT INTO `order_item_detail` (o_id, item_id, qty) VALUES (?, ?, ?)";
                orderItemStatement = connection.prepareStatement(itemQuery);
                orderItemStatement.setString(1, lastId);
                orderItemStatement.setString(2, parts[0]);
                orderItemStatement.setString(3, parts[1]);
                orderItemStatement.executeUpdate();
            }

            // Commit transaction
            connection.commit();

            // Send success response
            resp.getWriter().write("{\"status\":\"success\"}");
        } catch (ClassNotFoundException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"JDBC Driver not found.\"}");
            e.printStackTrace();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
            e.printStackTrace();
        } finally {
            // Close resources and set auto-commit back to true
            try {
                if (orderStatement != null) {
                    orderStatement.close();
                }
                if (lastIdStatement != null) {
                    lastIdStatement.close();
                }
                if (orderItemStatement != null) {
                    orderItemStatement.close();
                }
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit to true
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
