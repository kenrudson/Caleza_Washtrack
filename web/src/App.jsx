import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Register from "./features/auth/Register";
import Login from "./features/auth/Login";
import Dashboard from "./features/dashboard/Dashboard";
import { authRoutes } from "./features/auth/routes";
import { dashboardRoutes } from "./features/dashboard/routes";

// eslint-disable-next-line react/prop-types
function ProtectedRoute({ children }) {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to={authRoutes.login} replace />;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to={authRoutes.login} replace />} />
        <Route path={authRoutes.register} element={<Register />} />
        <Route path={authRoutes.login} element={<Login />} />
        <Route
          path={dashboardRoutes.dashboard}
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;


