import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { fetchCustomerOrders, fetchAllOrdersForStaff, advanceOrderStatus } from "./dashboardService";
import BellIcon from "./components/BellIcon";
import CustomerDashboard from "./CustomerDashboard";
import StaffDashboard from "./StaffDashboard";
import { MOCK_NOTIFICATIONS } from "./dashboardConstants";
import { getInitials, toDisplayOrder, toStaffDisplayOrder } from "./dashboardHelpers";
import "./Dashboard.css";

// ─── Main Dashboard Component ───────────────────────────────
export default function Dashboard() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const isStaff = user.role === "STAFF";

  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [activeNav, setActiveNav] = useState("dashboard");

  // Staff state — real data from the backend (FR-007)
  const [staffOrders, setStaffOrders] = useState([]);
  const [loadingStaffOrders, setLoadingStaffOrders] = useState(true);
  const [staffFetchError, setStaffFetchError] = useState("");

  // Customer order state — real data from the backend (FR-004, FR-005, FR-011)
  const [customerOrders, setCustomerOrders] = useState([]);
  const [loadingOrders, setLoadingOrders] = useState(true);
  const [ordersFetchError, setOrdersFetchError] = useState("");
  const [showNewOrderModal, setShowNewOrderModal] = useState(false);

  const loadCustomerOrders = () => {
    if (!user.userId) return;
    setLoadingOrders(true);
    fetchCustomerOrders(user.userId)
      .then((data) => {
        setCustomerOrders(data.map(toDisplayOrder));
        setOrdersFetchError("");
      })
      .catch(() => setOrdersFetchError("Could not load your orders right now."))
      .finally(() => setLoadingOrders(false));
  };

  const loadStaffOrders = () => {
    setLoadingStaffOrders(true);
    fetchAllOrdersForStaff()
      .then((data) => {
        setStaffOrders(data.map(toStaffDisplayOrder));
        setStaffFetchError("");
      })
      .catch(() => setStaffFetchError("Could not load the order queue right now."))
      .finally(() => setLoadingStaffOrders(false));
  };

  useEffect(() => {
    if (isStaff) {
      loadStaffOrders();
    } else {
      loadCustomerOrders();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user.userId, isStaff]);

  const handleOrderCreated = (newOrderResponse) => {
    setCustomerOrders((prev) => [toDisplayOrder(newOrderResponse), ...prev]);
    setShowNewOrderModal(false);
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  // FR-007: advance the order to its next status via the real backend, then refresh the queue
  const handleStatusUpdate = (orderId) => {
    advanceOrderStatus(orderId)
      .then(() => loadStaffOrders())
      .catch(() => setStaffFetchError("Could not update that order's status. Please try again."));
  };

  // NOTE: payment recording does not yet have a backend endpoint — this still only
  // updates local state and will not persist. Flagged as a known follow-up item.
  const handleRecordPayment = (orderId) => {
    setStaffOrders((prev) =>
      prev.map((o) => (o.id === orderId ? { ...o, paid: true } : o))
    );
  };

  const unreadCount = MOCK_NOTIFICATIONS.filter((n) => !n.read).length;

  return (
    <div className="dashboard-shell">
      {/* Sidebar Overlay (mobile) */}
      <div
        className={`sidebar-overlay ${sidebarOpen ? "active" : ""}`}
        onClick={() => setSidebarOpen(false)}
      />

      {/* ── Sidebar ───────────────────────────────── */}
      <aside className={`sidebar ${sidebarOpen ? "open" : ""}`}>
        <div className="sidebar-brand">
          <div className="brand-icon">🫧</div>
          <div className="brand-text">
            <span className="brand-name">WashTrack</span>
            <span className="brand-subtitle">Laundry Management</span>
          </div>
        </div>

        <nav className="sidebar-nav">
          <span className="sidebar-section-label">Main</span>
          <button
            className={`nav-item ${activeNav === "dashboard" ? "active" : ""}`}
            onClick={() => { setActiveNav("dashboard"); setSidebarOpen(false); }}
          >
            <span className="nav-icon">📊</span>
            Dashboard
          </button>

          {!isStaff ? (
            <>
              <button
                className={`nav-item ${activeNav === "orders" ? "active" : ""}`}
                onClick={() => { setActiveNav("orders"); setSidebarOpen(false); }}
              >
                <span className="nav-icon">📋</span>
                My Orders
                <span className="nav-badge">
                  {customerOrders.filter((o) => o.status !== "Delivered").length}
                </span>
              </button>
              <button
                className={`nav-item ${activeNav === "pickup" ? "active" : ""}`}
                onClick={() => { setActiveNav("pickup"); setSidebarOpen(false); }}
              >
                <span className="nav-icon">📅</span>
                Schedule Pickup
              </button>
              <button
                className={`nav-item ${activeNav === "neworder" ? "active" : ""}`}
                onClick={() => { setActiveNav("neworder"); setSidebarOpen(false); }}
              >
                <span className="nav-icon">➕</span>
                New Order
              </button>
            </>
          ) : (
            <>
              <button
                className={`nav-item ${activeNav === "manage" ? "active" : ""}`}
                onClick={() => { setActiveNav("manage"); setSidebarOpen(false); }}
              >
                <span className="nav-icon">📦</span>
                Manage Orders
                <span className="nav-badge">
                  {staffOrders.filter((o) => o.status !== "Delivered").length}
                </span>
              </button>
              <button
                className={`nav-item ${activeNav === "payments" ? "active" : ""}`}
                onClick={() => { setActiveNav("payments"); setSidebarOpen(false); }}
              >
                <span className="nav-icon">💰</span>
                Payments
              </button>
            </>
          )}

          <span className="sidebar-section-label">Account</span>
          <button
            className={`nav-item ${activeNav === "notifications" ? "active" : ""}`}
            onClick={() => { setActiveNav("notifications"); setSidebarOpen(false); }}
          >
            <span className="nav-icon"><BellIcon size={18} /></span>
            Notifications
            {unreadCount > 0 && <span className="nav-badge">{unreadCount}</span>}
          </button>
          <button
            className={`nav-item ${activeNav === "profile" ? "active" : ""}`}
            onClick={() => { setActiveNav("profile"); setSidebarOpen(false); }}
          >
            <span className="nav-icon">👤</span>
            Profile
          </button>
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-user">
            <div className="user-avatar">{getInitials(user.fullName || "User")}</div>
            <div className="user-info">
              <span className="user-name">{user.fullName || "User"}</span>
              <span className="user-role">{user.role || "CUSTOMER"}</span>
            </div>
          </div>
        </div>
      </aside>

      {/* ── Main Content ──────────────────────────── */}
      <main className="main-content">
        {/* Top Header */}
        <header className="top-header">
          <div className="header-left">
            <button className="mobile-menu-btn" onClick={() => setSidebarOpen(true)}>
              ☰
            </button>
            <div>
              <div className="page-title">
                {isStaff ? "Staff Dashboard" : "My Dashboard"}
              </div>
              <div className="page-subtitle">
                {new Date().toLocaleDateString("en-US", {
                  weekday: "long",
                  year: "numeric",
                  month: "long",
                  day: "numeric",
                })}
              </div>
            </div>
          </div>

          <div className="header-right">
            <div style={{ position: "relative" }}>
              <button
                className="notification-btn"
                onClick={() => setShowNotifications(!showNotifications)}
                title="Notifications"
              >
                <BellIcon size={20} />
                {unreadCount > 0 && <span className="notif-count">{unreadCount}</span>}
              </button>

              {showNotifications && (
                <div className="notification-dropdown">
                  <div className="dropdown-header">
                    <h3>Notifications</h3>
                    <button className="btn-ghost" style={{ fontSize: "0.75rem" }}>
                      Mark all read
                    </button>
                  </div>
                  <div className="dropdown-body">
                    {MOCK_NOTIFICATIONS.map((n) => (
                      <div className="notification-item" key={n.id}>
                        <div className={`notif-dot ${n.read ? "read" : ""}`} />
                        <div className="notif-content">
                          <div className="notif-text">{n.text}</div>
                          <div className="notif-time">{n.time}</div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>

            <button className="logout-btn" onClick={handleLogout}>
              Log Out
            </button>
          </div>
        </header>

        {/* Page Content */}
        <div className="page-content">
          {isStaff ? (
            <StaffDashboard
              user={user}
              orders={staffOrders}
              loadingOrders={loadingStaffOrders}
              fetchError={staffFetchError}
              onStatusUpdate={handleStatusUpdate}
              onRecordPayment={handleRecordPayment}
            />
          ) : (
            <CustomerDashboard
              user={user}
              orders={customerOrders}
              loadingOrders={loadingOrders}
              fetchError={ordersFetchError}
              showNewOrderModal={showNewOrderModal}
              onOpenNewOrder={() => setShowNewOrderModal(true)}
              onCloseNewOrder={() => setShowNewOrderModal(false)}
              onOrderCreated={handleOrderCreated}
            />
          )}
        </div>
      </main>
    </div>
  );
}

