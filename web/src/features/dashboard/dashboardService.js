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

// Interim lightweight payment marking (see backend StaffOrderService for scope note)
export async function markOrderAsPaid(orderId) {
  const response = await axiosClient.post(`/staff/orders/${orderId}/mark-paid`);
  return response.data;
}

// FR-010: real notifications, for both CUSTOMER and STAFF accounts
export async function fetchMyNotifications(userId) {
  const response = await axiosClient.get(`/notifications/my/${userId}`);
  return response.data;
}

export async function markNotificationsRead(userId) {
  const response = await axiosClient.post(`/notifications/my/${userId}/mark-read`);
  return response.data;
}
