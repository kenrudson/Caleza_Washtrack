<<<<<<< HEAD:web/src/pages/Register.jsx
import RegisterPage from "../features/auth/register/RegisterPage";
=======
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerUser } from "./authService";
import { authRoutes } from "./routes";
import { dashboardRoutes } from "../dashboard/routes";
>>>>>>> ccf7463243dfe01ba11fe0586113bc7eecfb4ea5:web/src/features/auth/Register.jsx

export default RegisterPage;
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: "", email: "", phone: "", address: "", password: "",
  });
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (form.password !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    setLoading(true);
    try {
      const data = await registerUser(form);
      localStorage.setItem("token", data.token);
      localStorage.setItem("user", JSON.stringify(data));
      navigate(dashboardRoutes.dashboard);
    } catch (err) {
      setError(err.response?.data?.message || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <h2>Create Account</h2>
      <form onSubmit={handleSubmit}>
        <input name="fullName" placeholder="Full Name" value={form.fullName} onChange={handleChange} required />
        <input name="email" type="email" placeholder="Email" value={form.email} onChange={handleChange} required />
        <input name="phone" placeholder="Phone Number" value={form.phone} onChange={handleChange} required />
        <input name="address" placeholder="Address" value={form.address} onChange={handleChange} required />
        <input name="password" type="password" placeholder="Password" value={form.password} onChange={handleChange} required minLength={8} />
        <input type="password" placeholder="Confirm Password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required />

        {error && <p className="error">{error}</p>}

        <button type="submit" disabled={loading}>
          {loading ? "Creating account..." : "Register"}
        </button>
      </form>
      <p>Already have an account? <Link to={authRoutes.login}>Log in</Link></p>
    </div>
  );
}
