import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { fetchCustomerOrders } from "./dashboardService";
import "./Dashboard.css";

// eslint-disable-next-line react/prop-types
function BellIcon({ size = 18 }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M18 8a6 6 0 0 0-12 0c0 7-3 9-3 9h18s-3-2-3-9" />
      <path d="M13.73 21a2 2 0 0 1-3.46 0" />
    </svg>
  );
}

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

const STATUS_STEPS = ["Pending", "Delivered", "Processing", "Ready", "Picked Up"];

function getInitials(name) {
  return name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);
}

function getNextStatus(current) {
  const idx = STATUS_STEPS.indexOf(current);
  if (idx < STATUS_STEPS.length - 1) return STATUS_STEPS[idx + 1];
  return null;
}

export default function Dashboard() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const isStaff = user.role === "STAFF";

  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [activeNav, setActiveNav] = useState("dashboard");
  const [staffOrders, setStaffOrders] = useState(MOCK_STAFF_ORDERS);
  const [customerOrders, setCustomerOrders] = useState([]);
  const [loadingOrders, setLoadingOrders] = useState(true);
  const [ordersFetchError, setOrdersFetchError] = useState("");
  const [showNewOrderModal, setShowNewOrderModal] = useState(false);

  const loadCustomerOrders = async () => {
    if (!user.userId) return;
    setLoadingOrders(true);
    try {
      const data = await fetchCustomerOrders(user.userId);
      setCustomerOrders(data.map(toDisplayOrder));
      setOrdersFetchError("");
    } catch {
      setOrdersFetchError("Could not load your orders right now.");
    } finally {
      setLoadingOrders(false);
    }
  };

  useEffect(() => {
    if (!isStaff) loadCustomerOrders();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user.userId, isStaff]);

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
      <div
        className={`sidebar-overlay ${sidebarOpen ? "active" : ""}`}
        onClick={() => setSidebarOpen(false)}
      />

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

      <main className="dashboard-main">
        <header className="dashboard-topbar">
          <button className="menu-toggle" onClick={() => setSidebarOpen(!sidebarOpen)}>
            ☰
          </button>
          <div className="dashboard-header">
            <h1>{isStaff ? "Staff Dashboard" : "Welcome back"}</h1>
            <p>{isStaff ? "Manage orders and payments" : "Track your laundry orders"}</p>
          </div>
          <div className="topbar-actions">
            <button className="notification-button" onClick={() => setShowNotifications(!showNotifications)}>
              <BellIcon size={20} />
              {unreadCount > 0 && <span className="notification-count">{unreadCount}</span>}
            </button>
            <button className="logout-button" onClick={handleLogout}>Logout</button>
          </div>
        </header>

        <section className="dashboard-content">
          {activeNav === "dashboard" && (
            <div className="dashboard-panel">
              <div className="stats-grid">
                <div className="stat-card">
                  <h3>Total Active Orders</h3>
                  <p>{isStaff ? staffOrders.filter((o) => o.status !== "Delivered").length : customerOrders.filter((o) => o.status !== "Delivered").length}</p>
                </div>
                <div className="stat-card">
                  <h3>Pending Payments</h3>
                  <p>{isStaff ? staffOrders.filter((o) => !o.paid).length : customerOrders.filter((o) => !o.paid).length}</p>
                </div>
                <div className="stat-card">
                  <h3>Last Updated</h3>
                  <p>{new Date().toLocaleDateString()}</p>
                </div>
              </div>
            </div>
          )}

          {activeNav === "orders" && (
            <div className="dashboard-panel">
              <h2>My Orders</h2>
              {loadingOrders ? (
                <p>Loading your orders...</p>
              ) : ordersFetchError ? (
                <p className="error">{ordersFetchError}</p>
              ) : (
                <div className="order-table">
                  {customerOrders.map((order) => (
                    <div key={order.id} className="order-row">
                      <div>{order.id}</div>
                      <div>{order.service}</div>
                      <div>{order.weight} kg</div>
                      <div>{order.status}</div>
                      <div>{order.paid ? "Paid" : "Pending"}</div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {activeNav === "pickup" && (
            <div className="dashboard-panel">
              <h2>Schedule Pickup</h2>
              <p>Pickup scheduling is available soon.</p>
            </div>
          )}

          {activeNav === "neworder" && (
            <div className="dashboard-panel">
              <h2>New Order</h2>
              <button className="primary-button" onClick={() => setShowNewOrderModal(true)}>Create New Order</button>
              {showNewOrderModal && (
                <div className="modal">
                  <div className="modal-content">
                    <h3>New order flow coming soon</h3>
                    <button className="secondary-button" onClick={() => setShowNewOrderModal(false)}>Close</button>
                  </div>
                </div>
              )}
            </div>
          )}

          {activeNav === "manage" && (
            <div className="dashboard-panel">
              <h2>Manage Orders</h2>
              <div className="order-table">
                {staffOrders.map((order) => (
                  <div key={order.id} className="order-row">
                    <div>{order.id}</div>
                    <div>{order.customer}</div>
                    <div>{order.service}</div>
                    <div>{order.status}</div>
                    <div>
                      <button className="small-button" onClick={() => handleStatusUpdate(order.id)}>Advance</button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeNav === "payments" && (
            <div className="dashboard-panel">
              <h2>Payments</h2>
              <div className="order-table">
                {staffOrders.map((order) => (
                  <div key={order.id} className="order-row">
                    <div>{order.id}</div>
                    <div>{order.customer}</div>
                    <div>{order.price}</div>
                    <div>{order.paid ? "Paid" : "Pending"}</div>
                    <div>
                      {!order.paid && (
                        <button className="small-button" onClick={() => handleRecordPayment(order.id)}>Record Payment</button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeNav === "notifications" && (
            <div className="dashboard-panel">
              <h2>Notifications</h2>
              <div className="notification-list">
                {MOCK_NOTIFICATIONS.map((notification) => (
                  <div key={notification.id} className={`notification-item ${notification.read ? "read" : "unread"}`}>
                    <p>{notification.text}</p>
                    <span>{notification.time}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeNav === "profile" && (
            <div className="dashboard-panel">
              <h2>Profile</h2>
              <div className="profile-details">
                <div><strong>Name:</strong> {user.fullName || "—"}</div>
                <div><strong>Email:</strong> {user.email || "—"}</div>
                <div><strong>Role:</strong> {user.role || "CUSTOMER"}</div>
              </div>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

function toDisplayOrder(order) {
  return {
    id: order.id,
    service: order.service,
    weight: order.weight,
    status: order.status,
    paid: order.paid,
  };
}
