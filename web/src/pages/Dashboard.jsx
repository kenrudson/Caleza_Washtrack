import { useNavigate } from "react-router-dom";

export default function Dashboard() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "{}");

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <div>
      <h2>Welcome, {user.fullName || "User"}!</h2>
      <p>Role: {user.role}</p>
      <button onClick={handleLogout}>Log Out</button>
    </div>
  );
}