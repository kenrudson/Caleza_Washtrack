import axiosClient from "../../api/axiosClient";

export async function fetchCustomerOrders(userId) {
  const response = await axiosClient.get(`/orders/my/${userId}`);
  return response.data;
}

export async function submitNewOrder(orderData) {
  const response = await axiosClient.post("/orders/new", orderData);
  return response.data;
}
