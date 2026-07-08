import axiosClient from "../../../infrastructure/api/axiosClient";

export async function registerUser(formData) {
  const response = await axiosClient.post("/auth/register", formData);
  return response.data;
}
