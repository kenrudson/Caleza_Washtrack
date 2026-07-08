import axiosClient from "../../../infrastructure/api/axiosClient";

export async function loginUser({ email, password }) {
  const response = await axiosClient.post("/auth/login", { email, password });
  return response.data;
}
