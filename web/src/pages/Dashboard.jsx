import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../App.css";

// ─── Mock Data ────────────────────────────────────────────────
const MOCK_CUSTOMER_ORDERS = [
  { id: "ORD-1042", service: "Wash & Fold", weight: 5.2, price: 260, status: "Processing", date: "2026-07-04", paid: false },
  { id: "ORD-1038", service: "Dry Clean", weight: 2.1, price: 315, status: "Ready", date: "2026-07-03", paid: false },
  { id: "ORD-1035", service: "Wash & Fold", weight: 3.8, price: 190, status: "Delivered", date: "2026-07-01", paid: true },
  { id: "ORD-1029", service: "Fold Only", weight: 4.0, price: 160, status: "Delivered", date: "2026-06-28", paid: true },
  { id: "ORD-1021", service: "Dry Clean", weight: 1.5, price: 225, status: "Delivered", date: "2026-06-25", paid: true },
];

const MOCK_STAFF_ORDERS = [
  { id: "ORD-1042", customer: "Maria Santos", service: "Wash & Fold", weight: 5.2, price: 260, status: "Processing", date: "2026-07-04", paid: false },
  { id: "ORD-1041", customer: "Juan Dela Cruz", service: "Dry Clean", weight: 3.0, price: 450, status: "Pending", date: "2026-07-04", paid: false },
  { id: "ORD-1040", customer: "Ana Reyes", service: "Wash & Fold", weight: 6.5, price: 325, status: "Picked Up", date: "2026-07-04", paid: false },
  { id: "ORD-1038", customer: "Pedro Garcia", service: "Dry Clean", weight: 2.1, price: 315, status: "Ready", date: "2026-07-03", paid: false },
  { id: "ORD-1037", customer: "Lisa Tan", service: "Fold Only", weight: 4.2, price: 168, status: "Ready", date: "2026-07-03", paid: true },
  { id: "ORD-1035", customer: "Maria Santos", service: "Wash & Fold", weight: 3.8, price: 190, status: "Delivered", date: "2026-07-01", paid: true },
];

const MOCK_NOTIFICATIONS = [
  { id: 1, text: "Your order ORD-1042 is now being processed.", time: "2 hours ago", read: false },
  { id: 2, text: "Order ORD-1038 is ready for pickup!", time: "5 hours ago", read: false },
  { id: 3, text: "Your order ORD-1035 has been delivered.", time: "2 days ago", read: true },
  { id: 4, text: "Payment received for ORD-1035. Thank you!", time: "2 days ago", read: true },
];

const STATUS_STEPS = ["Pending", "Picked Up", "Processing", "Ready", "Delivered"];

const SERVICE_ICONS = {
  "Wash & Fold": "🧺",
  "Dry Clean": "👔",
  "Fold Only": "👕",
};

const STATUS_CLASS = {
  Pending: "pending",
  "Picked Up": "picked-up",
  Processing: "processing",
  Ready: "ready",
  Delivered: "delivered",
};

// ─── Utility: get status step index ─────────────────────────
function getStatusIndex(status) {
  return STATUS_STEPS.indexOf(status);
}

// ─── Utility: get initials ──────────────────────────────────
function getInitials(name) {
  return name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);
}

// ─── Utility: get next status ───────────────────────────────
function getNextStatus(current) {
  const idx = STATUS_STEPS.indexOf(current);
  if (idx < STATUS_STEPS.length - 1) return STATUS_STEPS[idx + 1];
  return null;
}

// ─── Main Dashboard Component ───────────────────────────────
export default function Dashboard() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const isStaff = user.role === "STAFF";

  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [activeNav, setActiveNav] = useState("dashboard");

  // Staff state for managing orders
  const [staffOrders, setStaffOrders] = useState(MOCK_STAFF_ORDERS);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const handleStatusUpdate = (orderId) => {
    setStaffOrders((prev) =>
      prev.map((o) => {
        if (o.id === orderId) {
          const next = getNextStatus(o.status);
          return next ? { ...o, status: next } : o;
        }
        return o;
      })
    );
  };

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
                  {MOCK_CUSTOMER_ORDERS.filter((o) => o.status !== "Delivered").length}
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
            <span className="nav-icon">🔔</span>
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
                🔔
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
              onStatusUpdate={handleStatusUpdate}
              onRecordPayment={handleRecordPayment}
            />
          ) : (
            <CustomerDashboard user={user} />
          )}
        </div>
      </main>
    </div>
  );
}

// ─── Customer Dashboard ─────────────────────────────────────
function CustomerDashboard({ user }) {
  const activeOrders = MOCK_CUSTOMER_ORDERS.filter((o) => o.status !== "Delivered");
  const latestOrder = activeOrders[0];
  const totalOrders = MOCK_CUSTOMER_ORDERS.length;
  const deliveredCount = MOCK_CUSTOMER_ORDERS.filter((o) => o.status === "Delivered").length;
  const totalSpent = MOCK_CUSTOMER_ORDERS.filter((o) => o.paid).reduce((sum, o) => sum + o.price, 0);

  return (
    <>
      {/* Welcome Banner */}
      <div className="welcome-banner animate-fade-in">
        <h1>Welcome back, {user.fullName?.split(" ")[0] || "there"}! 👋</h1>
        <p>
          {activeOrders.length > 0
            ? `You have ${activeOrders.length} active order${activeOrders.length > 1 ? "s" : ""} in progress.`
            : "You have no active orders. Ready to schedule a pickup?"}
        </p>
      </div>

      {/* Stat Cards */}
      <div className="stats-grid">
        <div className="stat-card animate-fade-in stagger-1">
          <div className="stat-header">
            <div className="stat-icon" style={{ background: "var(--accent-subtle)", color: "var(--accent-primary)" }}>📦</div>
            <span className="stat-trend up">All time</span>
          </div>
          <div className="stat-value">{totalOrders}</div>
          <div className="stat-label">Total Orders</div>
        </div>

        <div className="stat-card animate-fade-in stagger-2">
          <div className="stat-header">
            <div className="stat-icon" style={{ background: "var(--status-processing-bg)", color: "var(--status-processing)" }}>🔄</div>
          </div>
          <div className="stat-value">{activeOrders.length}</div>
          <div className="stat-label">Active Orders</div>
        </div>

        <div className="stat-card animate-fade-in stagger-3">
          <div className="stat-header">
            <div className="stat-icon" style={{ background: "var(--status-ready-bg)", color: "var(--status-ready)" }}>✅</div>
          </div>
          <div className="stat-value">{deliveredCount}</div>
          <div className="stat-label">Completed</div>
        </div>

        <div className="stat-card animate-fade-in stagger-4">
          <div className="stat-header">
            <div className="stat-icon" style={{ background: "var(--status-pending-bg)", color: "var(--status-pending)" }}>💵</div>
          </div>
          <div className="stat-value">₱{totalSpent.toLocaleString()}</div>
          <div className="stat-label">Total Spent</div>
        </div>
      </div>

      {/* Active Order Tracker + Quick Actions */}
      <div className="content-grid">
        {/* Active Order Tracker */}
        <div className="section-card animate-fade-in stagger-5">
          <div className="section-header">
            <div className="section-title">
              <span className="title-icon">📍</span>
              Active Order Tracker
            </div>
            {latestOrder && (
              <span className={`status-badge ${STATUS_CLASS[latestOrder.status]}`}>
                {latestOrder.status}
              </span>
            )}
          </div>
          <div className="section-body">
            {latestOrder ? (
              <>
                <div style={{ marginBottom: "12px", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <span className="order-id">{latestOrder.id}</span>
                  <span style={{ fontSize: "0.8rem", color: "var(--text-secondary)" }}>
                    {latestOrder.service} · {latestOrder.weight}kg
                  </span>
                </div>
                <div className="order-stepper">
                  {STATUS_STEPS.map((step, i) => {
                    const currentIdx = getStatusIndex(latestOrder.status);
                    let cls = "";
                    if (i < currentIdx) cls = "completed";
                    else if (i === currentIdx) cls = "active";
                    return (
                      <div className={`stepper-step ${cls}`} key={step}>
                        <div className="step-dot">
                          {i < currentIdx ? "✓" : i + 1}
                        </div>
                        <span className="step-label">{step}</span>
                      </div>
                    );
                  })}
                </div>
              </>
            ) : (
              <p style={{ color: "var(--text-muted)", textAlign: "center", padding: "32px 0" }}>
                No active orders to track.
              </p>
            )}
          </div>
        </div>

        {/* Quick Actions */}
        <div className="section-card animate-fade-in stagger-6">
          <div className="section-header">
            <div className="section-title">
              <span className="title-icon">⚡</span>
              Quick Actions
            </div>
          </div>
          <div className="section-body">
            <div className="quick-actions">
              <button className="quick-action-btn">
                <div className="action-icon" style={{ background: "var(--accent-subtle)", color: "var(--accent-primary)" }}>📅</div>
                <span className="action-label">Schedule Pickup</span>
                <span className="action-desc">Book a pickup date</span>
              </button>
              <button className="quick-action-btn">
                <div className="action-icon" style={{ background: "var(--status-ready-bg)", color: "var(--status-ready)" }}>➕</div>
                <span className="action-label">New Order</span>
                <span className="action-desc">Create laundry order</span>
              </button>
              <button className="quick-action-btn">
                <div className="action-icon" style={{ background: "var(--status-pending-bg)", color: "var(--status-pending)" }}>📋</div>
                <span className="action-label">Order History</span>
                <span className="action-desc">View past orders</span>
              </button>
              <button className="quick-action-btn">
                <div className="action-icon" style={{ background: "var(--status-processing-bg)", color: "var(--status-processing)" }}>👤</div>
                <span className="action-label">My Profile</span>
                <span className="action-desc">Update your info</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Orders Table */}
      <div className="section-card animate-fade-in" style={{ animationDelay: "0.35s", opacity: 0 }}>
        <div className="section-header">
          <div className="section-title">
            <span className="title-icon">📋</span>
            Recent Orders
          </div>
          <button className="btn-ghost">View All →</button>
        </div>
        <div className="section-body" style={{ padding: 0 }}>
          <table className="orders-table">
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Service</th>
                <th>Weight</th>
                <th>Price</th>
                <th>Status</th>
                <th>Payment</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {MOCK_CUSTOMER_ORDERS.map((order) => (
                <tr key={order.id}>
                  <td><span className="order-id">{order.id}</span></td>
                  <td>
                    <span className="order-service">
                      <span className="service-icon">{SERVICE_ICONS[order.service] || "🧺"}</span>
                      {order.service}
                    </span>
                  </td>
                  <td>{order.weight} kg</td>
                  <td><span className="order-price">₱{order.price}</span></td>
                  <td><span className={`status-badge ${STATUS_CLASS[order.status]}`}>{order.status}</span></td>
                  <td>
                    <span className={`payment-badge ${order.paid ? "paid" : "unpaid"}`}>
                      {order.paid ? "✓ Paid" : "Unpaid"}
                    </span>
                  </td>
                  <td style={{ color: "var(--text-secondary)", fontSize: "0.82rem" }}>{order.date}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
}

// ─── Staff Dashboard ────────────────────────────────────────
function StaffDashboard({ user, orders, onStatusUpdate, onRecordPayment }) {
  const pendingCount = orders.filter((o) => o.status === "Pending").length;
  const processingCount = orders.filter((o) => ["Picked Up", "Processing"].includes(o.status)).length;
  const readyCount = orders.filter((o) => o.status === "Ready").length;
  const deliveredToday = orders.filter((o) => o.status === "Delivered" && o.date === "2026-07-01").length;
  const unpaidCount = orders.filter((o) => !o.paid && o.status !== "Pending").length;
  const totalRevenue = orders.filter((o) => o.paid).reduce((sum, o) => sum + o.price, 0);

  return (
    <>
      {/* Welcome Banner */}
      <div className="welcome-banner animate-fade-in">
        <h1>Good {getTimeGreeting()}, {user.fullName?.split(" ")[0] || "Staff"}! 💼</h1>
        <p>
          You have {pendingCount} pending order{pendingCount !== 1 ? "s" : ""} and {unpaidCount} unpaid order{unpaidCount !== 1 ? "s" : ""} awaiting action.
        </p>
      </div>

      {/* Stat Cards */}
      <div className="stats-grid">
        <div className="stat-card animate-fade-in stagger-1">
          <div className="stat-header">
            <div className="stat-icon" style={{ background: "var(--status-pending-bg)", color: "var(--status-pending)" }}>⏳</div>
          </div>
          <div className="stat-value">{pendingCount}</div>
          <div className="stat-label">Pending Orders</div>
        </div>

        <div className="stat-card animate-fade-in stagger-2">
          <div className="stat-header">
            <div className="stat-icon" style={{ background: "var(--status-processing-bg)", color: "var(--status-processing)" }}>🔄</div>
          </div>
          <div className="stat-value">{processingCount}</div>
          <div className="stat-label">In Progress</div>
        </div>

        <div className="stat-card animate-fade-in stagger-3">
          <div className="stat-header">
            <div className="stat-icon" style={{ background: "var(--status-ready-bg)", color: "var(--status-ready)" }}>✅</div>
          </div>
          <div className="stat-value">{readyCount}</div>
          <div className="stat-label">Ready for Pickup</div>
        </div>

        <div className="stat-card animate-fade-in stagger-4">
          <div className="stat-header">
            <div className="stat-icon" style={{ background: "var(--accent-subtle)", color: "var(--accent-primary)" }}>💰</div>
          </div>
          <div className="stat-value">₱{totalRevenue.toLocaleString()}</div>
          <div className="stat-label">Revenue Collected</div>
        </div>
      </div>

      {/* Order Queue + Stats */}
      <div className="content-grid">
        {/* Order Management Queue */}
        <div className="section-card animate-fade-in stagger-5">
          <div className="section-header">
            <div className="section-title">
              <span className="title-icon">📦</span>
              Order Queue
            </div>
            <span style={{ fontSize: "0.75rem", color: "var(--text-muted)" }}>
              {orders.filter((o) => o.status !== "Delivered").length} active
            </span>
          </div>
          <div className="section-body">
            {orders
              .filter((o) => o.status !== "Delivered")
              .map((order) => (
                <div className="order-queue-item" key={order.id}>
                  <div className="queue-info">
                    <div className="queue-customer">
                      <span className="order-id" style={{ marginRight: "8px" }}>{order.id}</span>
                      {order.customer}
                    </div>
                    <div className="queue-details">
                      {SERVICE_ICONS[order.service]} {order.service} · {order.weight}kg · ₱{order.price}
                    </div>
                  </div>
                  <span className={`status-badge ${STATUS_CLASS[order.status]}`}>{order.status}</span>
                  <div className="queue-actions">
                    {getNextStatus(order.status) && (
                      <button className="btn-primary" onClick={() => onStatusUpdate(order.id)}>
                        → {getNextStatus(order.status)}
                      </button>
                    )}
                  </div>
                </div>
              ))}
          </div>
        </div>

        {/* Payments Overview */}
        <div className="section-card animate-fade-in stagger-6">
          <div className="section-header">
            <div className="section-title">
              <span className="title-icon">💳</span>
              Payment Status
            </div>
          </div>
          <div className="section-body">
            {orders
              .filter((o) => o.status !== "Pending")
              .map((order) => (
                <div className="order-queue-item" key={order.id}>
                  <div className="queue-info">
                    <div className="queue-customer">
                      <span className="order-id" style={{ marginRight: "8px" }}>{order.id}</span>
                      {order.customer}
                    </div>
                    <div className="queue-details">
                      ₱{order.price} · {order.service}
                    </div>
                  </div>
                  <span className={`payment-badge ${order.paid ? "paid" : "unpaid"}`}>
                    {order.paid ? "✓ Paid" : "Unpaid"}
                  </span>
                  {!order.paid && (
                    <button className="btn-primary" style={{ fontSize: "0.72rem", padding: "5px 10px" }} onClick={() => onRecordPayment(order.id)}>
                      Record Payment
                    </button>
                  )}
                </div>
              ))}
          </div>
        </div>
      </div>

      {/* All Orders Table */}
      <div className="section-card animate-fade-in" style={{ animationDelay: "0.35s", opacity: 0 }}>
        <div className="section-header">
          <div className="section-title">
            <span className="title-icon">📋</span>
            All Orders
          </div>
        </div>
        <div className="section-body" style={{ padding: 0 }}>
          <table className="orders-table">
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Customer</th>
                <th>Service</th>
                <th>Weight</th>
                <th>Price</th>
                <th>Status</th>
                <th>Payment</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id}>
                  <td><span className="order-id">{order.id}</span></td>
                  <td>{order.customer}</td>
                  <td>
                    <span className="order-service">
                      <span className="service-icon">{SERVICE_ICONS[order.service] || "🧺"}</span>
                      {order.service}
                    </span>
                  </td>
                  <td>{order.weight} kg</td>
                  <td><span className="order-price">₱{order.price}</span></td>
                  <td><span className={`status-badge ${STATUS_CLASS[order.status]}`}>{order.status}</span></td>
                  <td>
                    <span className={`payment-badge ${order.paid ? "paid" : "unpaid"}`}>
                      {order.paid ? "✓ Paid" : "Unpaid"}
                    </span>
                  </td>
                  <td style={{ color: "var(--text-secondary)", fontSize: "0.82rem" }}>{order.date}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
}

// ─── Helper: Time-based greeting ────────────────────────────
function getTimeGreeting() {
  const hour = new Date().getHours();
  if (hour < 12) return "morning";
  if (hour < 17) return "afternoon";
  return "evening";
}