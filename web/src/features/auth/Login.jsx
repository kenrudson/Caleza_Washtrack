<<<<<<< HEAD:web/src/pages/Login.jsx
import LoginPage from "../features/auth/login/LoginPage";

export default LoginPage;
=======
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginUser } from "./authService";
import { authRoutes } from "./routes";
import { dashboardRoutes } from "../dashboard/routes";
>>>>>>> ccf7463243dfe01ba11fe0586113bc7eecfb4ea5:web/src/features/auth/Login.jsx

  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const data = await loginUser(form);
      localStorage.setItem("token", data.token);
      localStorage.setItem("user", JSON.stringify(data));
      navigate(dashboardRoutes.dashboard);
    } catch (err) {
      setError(err.response?.data?.message || "Login failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <h2>Log In</h2>
      <form onSubmit={handleSubmit}>
        <input name="email" type="email" placeholder="Email" value={form.email} onChange={handleChange} required />
        <input name="password" type="password" placeholder="Password" value={form.password} onChange={handleChange} required />

        {error && <p className="error">{error}</p>}

        <button type="submit" disabled={loading}>
          {loading ? "Logging in..." : "Log In"}
        </button>
      </form>
      <p>Don&apos;t have an account? <Link to={authRoutes.register}>Register</Link></p>
    </div>
  );
}
