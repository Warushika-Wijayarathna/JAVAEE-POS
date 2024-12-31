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

// once place order button is clicked, send the order to the server with order details


// Call the loadItems function when the page is ready
$(document).ready(function () {
    loadItems();
});
