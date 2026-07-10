import axiosClient from "../../api/axiosClient";

export async function fetchCustomerOrders(userId) {
  const response = await axiosClient.get(`/orders/my/${userId}`);
  return response.data;
}

export async function submitNewOrder(orderData) {
  const response = await axiosClient.post("/orders/new", orderData);
  return response.data;
}

// FR-007: staff-facing order queue and status advancement
export async function fetchAllOrdersForStaff() {
  const response = await axiosClient.get("/staff/orders");
  return response.data;
}

export async function advanceOrderStatus(orderId) {
  const response = await axiosClient.post(`/staff/orders/${orderId}/advance-status`);
  return response.data;
}
