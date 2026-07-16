import axiosClient from "../../api/axiosClient";

// FR-003: fetch the full profile for the given user
export async function getProfile(userId) {
  const response = await axiosClient.get(`/profile/${userId}`);
  return response.data;
}

// FR-003: update name, phone, address (email is read-only)
export async function updateProfile(userId, profileData) {
  const response = await axiosClient.put(`/profile/${userId}`, profileData);
  return response.data;
}

// FR-003: change password — requires the current password for verification
export async function changePassword(userId, passwordData) {
  const response = await axiosClient.put(`/profile/${userId}/change-password`, passwordData);
  return response.data;
}
