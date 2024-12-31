// Load all the items to the itemSelectionTableBody table
function loadItems() {
    $.ajax({
        url: "http://localhost:8080/GDSE/item",
        type: "GET",
        success: function (data) {
            var itemSelectionTableBody = $("#itemSelectionTableBody");
            itemSelectionTableBody.empty();
            $.each(data, function (index, item) {
                itemSelectionTableBody.append(
                    "<tr>" +
                    "<td>" + item.id + "</td>" +
                    "<td>" + item.name + "</td>" +
                    "<td>" + item.price + "</td>" +
                    "<td>" + item.qtyOnHand + "</td>" +
                    "<td><button class='btn btn-primary' onclick='addItemToOrder(" + JSON.stringify(item) + ")'>Add</button></td>" +
                    "</tr>"
                );
            });
        },
        error: function () {
            alert("Failed to load items!");
        }
    });
}

// Add the selected item to the cartTableBody table
function addItemToOrder(selectedItem) {
    var cartTableBody = $("#cartTableBody");

    // Check if the item is already in the cart
    var existingRow = cartTableBody.find("tr[data-id='" + selectedItem.id + "']");
    if (existingRow.length > 0) {
        // Increment the quantity and update the total price
        var qtyCell = existingRow.find(".qty");
        var qty = parseInt(qtyCell.text(), 10);
        qty += 1;
        qtyCell.text(qty);

        var totalCell = existingRow.find(".total");
        totalCell.text((qty * parseFloat(selectedItem.price)));
    } else {
        // Add a new row for the item
        cartTableBody.append(
            "<tr data-id='" + selectedItem.id + "'>" +
            "<td>" + selectedItem.id + "</td>" +
            "<td>" + selectedItem.name + "</td>" +
            "<td class='price'>" + selectedItem.price + "</td>" +
            "<td>" +
            "<button class='btn btn-sm btn-success' onclick='changeQty(this, 1)'>+</button>" +
            " <span class='qty'>1</span> " +
            "<button class='btn btn-sm btn-danger' onclick='changeQty(this, -1)'>-</button>" +
            "</td>" +
            "<td class='total'>" + selectedItem.price + "</td>" +
            "</tr>"
        );
    }
    updateOrderTotal();
}

// Adjust the quantity and update the total
function changeQty(button, delta) {
    var row = $(button).closest("tr");
    var qtyCell = row.find(".qty");
    var qty = parseInt(qtyCell.text(), 10);

    if (qty + delta > 0) {
        qty += delta;
        qtyCell.text(qty);

        var price = parseFloat(row.find(".price").text());
        var totalCell = row.find(".total");
        totalCell.text((qty * price));
    } else {
        // Remove the row if quantity is reduced to 0
        row.remove();
    }
    updateOrderTotal();
}

// Update the order total in the table
function updateOrderTotal() {
    var total = 0;
    $("#cartTableBody tr").each(function () {
        var totalCell = $(this).find(".total");
        total += parseFloat(totalCell.text());
    });
    $("#orderTotal").text(total);
    calculateBalance(); // Recalculate balance whenever total changes
}

// Calculate the balance based on the cash amount
function calculateBalance() {
    var orderTotal = parseFloat($("#orderTotal").text());
    var cashAmount = parseFloat($("#cashAmount").val());
    var balance = cashAmount - orderTotal;

    $("#balance").text(balance > 0 ? balance : "0.00");
}

// Get the next orderId from the server
function getOrderId() {

    // Fetch the next orderId from the last row of the orderTableBody table
    var orderTableBody = $("#orderTableBody");
    var lastRow = orderTableBody.find("tr:last");
    var orderId = 1;
    if (lastRow.length > 0) {
        orderId = parseInt(lastRow.find("td:first").text(), 10) + 1;
    }

    //use innerHTML to set the orderId
    document.getElementById("orderId").innerHTML = "#"+orderId;

}

function loadOrders() {
    // Fetch all orders from the server
    $.ajax({
        url: "http://localhost:8080/GDSE/order",
        type: "GET",
        success: function (data) {
            var orderTableBody = $("#orderTableBody");
            orderTableBody.empty();
            $.each(data, function (index, order) {
                orderTableBody.append(
                    "<tr>" +
                    "<td>" + order.id + "</td>" +
                    "<td>" + order.cust_id + "</td>" +
                    "<td>" + order.user_id + "</td>" +
                    "<td>" + order.total + "</td>" +
                    "<td>" + order.date + "</td>" +
                    // add a view btn
                    "<td><button class='btn btn-primary' onclick='viewOrder(" + order.id + ")'>View</button></td>" +
                    "</tr>"
                );
            });
        },
        error: function () {
            alert("Failed to load orders!");
        }
    });
}

// apply placeOrderButtonAction
function placeOrderButtonAction() {
    var order = {
        cust_id: $("#custId").val(),
        user_id: $("Admin").val(),
        total: $("#orderTotal").text(),
        date: new Date().toISOString().slice(0, 10)
    };

    // Fetch the items in the cart
    var items = [];
    $("#cartTableBody tr").each(function () {
        var row = $(this);
        items.push({
            item_id: parseInt(row.find("td:first").text(), 10),
            qty: parseInt(row.find(".qty").text(), 10)
        });
    });

    // Send the order and items to the server
    $.ajax({
        url: "http://localhost:8080/GDSE/order",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({order: order, items: items}),
        success: function () {
            alert("Order placed successfully!");
            loadOrders();  // Reload the order table
            getOrderId();  // Get the next orderId
        },
        error: function () {
            alert("Failed to place order!");
        }
    });
}


function viewOrder(orderId) {

}
// Call the loadItems function and getOrderId function when the page is ready
$(document).ready(function () {
    loadItems();
    getOrderId();  // Fetch the orderId when the page loads
    loadOrders();  // Fetch all orders when the page loads

    // Attach the placeOrderButtonAction to the Place Order button
    $("#placeOrderBtn").click(function () {
        placeOrderButtonAction();
    });
});
