import axiosClient from "../../infrastructure/api/axiosClient";

export async function fetchCustomerOrders(userId) {
  const response = await axiosClient.get(`/orders/my/${userId}`);
  return response.data;
}

export async function createNewOrder(orderData) {
  const response = await axiosClient.post("/orders/new", orderData);
  return response.data;
}
