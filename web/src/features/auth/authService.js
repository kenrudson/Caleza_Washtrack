import axiosClient from "../../api/axiosClient";

export async function loginUser(credentials) {
  const response = await axiosClient.post("/auth/login", credentials);
  return response.data;
}

export async function registerUser(profile) {
  const response = await axiosClient.post("/auth/register", profile);
  return response.data;
}
