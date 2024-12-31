import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/order_view")
public class OrderView extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MedplusCarePharmacy", "root", "Ijse@1234")) {

            // SQL query to fetch order details along with associated items
            String query = "SELECT o.o_id, o.cust_id, o.total, o.date, i.item_id, i.qty " +
                    "FROM `Order` o " +
                    "LEFT JOIN `order_item_detail` i ON o.o_id = i.o_id " +
                    "ORDER BY o.o_id"; // Ensure orders are ordered by o_id
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");

            boolean firstOrder = true;
            String currentOrderId = null;
            StringBuilder itemsJson = new StringBuilder();

            while (resultSet.next()) {
                String orderId = resultSet.getString("o_id");

                // Start a new order JSON object if a new order is found
                if (!orderId.equals(currentOrderId)) {
                    if (!firstOrder) {
                        // Close the items array and add the order object to the JSON list
                        if (itemsJson.length() > 0) {
                            // Remove the last comma from items if present
                            if (itemsJson.charAt(itemsJson.length() - 1) == ',') {
                                itemsJson.deleteCharAt(itemsJson.length() - 1);
                            }
                            itemsJson.append("]");
                        }

                        // Add the current order and its items
                        jsonBuilder.append(String.format(
                                "{\"id\":\"%s\", \"cust_id\":\"%s\", \"total\":\"%s\", \"date\":\"%s\", \"items\":%s},",
                                currentOrderId,
                                resultSet.getString("cust_id"),
                                resultSet.getString("total"),
                                resultSet.getString("date"),
                                itemsJson.toString()
                        ));

                        // Reset the itemsJson for the next order
                        itemsJson.setLength(0);
                    }

                    // Start a new order
                    currentOrderId = orderId;
                    itemsJson.append("[");

                    // Start the first order iteration
                    firstOrder = false;
                }

                // Check if item_id or qty are not null before appending to avoid null items
                String itemId = resultSet.getString("item_id");
                String qty = resultSet.getString("qty");

                if (itemId != null && qty != null) {
                    String itemJson = String.format(
                            "{\"item_id\":\"%s\", \"qty\":\"%s\"}",
                            itemId,
                            qty
                    );
                    itemsJson.append(itemJson).append(",");
                }
            }

            // Ensure the last order and items are added to the JSON
            if (itemsJson.length() > 0) {
                if (itemsJson.charAt(itemsJson.length() - 1) == ',') {
                    itemsJson.deleteCharAt(itemsJson.length() - 1);
                }
                itemsJson.append("]");
            }

            // Add the last order to the final JSON
            jsonBuilder.append(String.format(
                    "{\"id\":\"%s\", \"cust_id\":\"%s\", \"total\":\"%s\", \"date\":\"%s\", \"items\":%s}",
                    currentOrderId,
                    resultSet.getString("cust_id"),
                    resultSet.getString("total"),
                    resultSet.getString("date"),
                    itemsJson.toString()
            ));

            jsonBuilder.append("]");  // Close the array of orders

            resultSet.close();

            // Write the response back to the client
            resp.getWriter().write(jsonBuilder.toString());

            // Print the returning data for debugging
            System.out.println("\n\n\nReturning Data : " + jsonBuilder.toString());

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
            e.printStackTrace();
        }
    }
}
