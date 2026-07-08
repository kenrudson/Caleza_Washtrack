import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axiosClient from "../api/axiosClient";
import "../App.css";

// ─── Bell Icon (matches the outlined style used on mobile) ─────
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

// ─── Mock Data (Staff dashboard + notifications remain mock for now) ──
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

  // Customer order state — real data from the backend (FR-004, FR-005, FR-011)
  const [customerOrders, setCustomerOrders] = useState([]);
  const [loadingOrders, setLoadingOrders] = useState(true);
  const [ordersFetchError, setOrdersFetchError] = useState("");
  const [showNewOrderModal, setShowNewOrderModal] = useState(false);

  const fetchCustomerOrders = () => {
    if (!user.userId) return;
    setLoadingOrders(true);
    axiosClient
      .get(`/orders/my/${user.userId}`)
      .then((res) => {
        setCustomerOrders(res.data.map(toDisplayOrder));
        setOrdersFetchError("");
      })
      .catch(() => setOrdersFetchError("Could not load your orders right now."))
      .finally(() => setLoadingOrders(false));
  };

  useEffect(() => {
    if (!isStaff) fetchCustomerOrders();
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

// ─── Customer Dashboard ─────────────────────────────────────
// ─── Backend <-> Display mapping ─────────────────────────────
const SERVICE_TYPE_LABELS = {
  WASH_FOLD: "Wash & Fold",
  DRY_CLEAN: "Dry Clean",
  FOLD_ONLY: "Fold Only",
};

const STATUS_LABELS = {
  PENDING: "Pending",
  PICKED_UP: "Picked Up",
  PROCESSING: "Processing",
  READY: "Ready",
  DELIVERED: "Delivered",
};

// Converts a backend OrderResponse into the shape the existing UI expects
function toDisplayOrder(o) {
  return {
    id: `ORD-${1000 + o.orderId}`,
    service: SERVICE_TYPE_LABELS[o.serviceType] || o.serviceType,
    weight: o.weightKg,
    price: o.totalPrice,
    status: STATUS_LABELS[o.status] || o.status,
    date: (o.createdAt || "").split("T")[0],
    paid: o.paid,
  };
}

// Minimum selectable pickup date: tomorrow, per BR-002 (same-day not permitted)
function getMinPickupDate() {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  return d.toISOString().split("T")[0];
}

const TIME_SLOTS = [
  "8:00 AM - 11:00 AM",
  "11:00 AM - 2:00 PM",
  "2:00 PM - 5:00 PM",
  "5:00 PM - 8:00 PM",
];

// ─── New Order Modal (FR-004 + FR-005) ───────────────────────
function NewOrderModal({ userId, onClose, onCreated }) {
  const [form, setForm] = useState({
    pickupAddress: "",
    scheduledDate: "",
    timeSlot: TIME_SLOTS[0],
    pickupNotes: "",
    serviceType: "WASH_FOLD",
    weightKg: "",
    specialInstructions: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!form.pickupAddress || !form.scheduledDate || !form.weightKg) {
      setError("Please fill in all required fields.");
      return;
    }
    const weight = parseFloat(form.weightKg);
    if (isNaN(weight) || weight <= 0 || weight > 50) {
      setError("Weight must be greater than 0 kg and at most 50 kg."); // BR-008
      return;
    }

    setLoading(true);
    try {
      const res = await axiosClient.post("/orders/new", {
        userId,
        pickupAddress: form.pickupAddress,
        scheduledDate: form.scheduledDate,
        timeSlot: form.timeSlot,
        pickupNotes: form.pickupNotes,
        serviceType: form.serviceType,
        weightKg: weight,
        specialInstructions: form.specialInstructions,
      });
      onCreated(res.data);
    } catch (err) {
      setError(err.response?.data?.message || "Could not create order. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>New Order</h3>
          <button className="modal-close-btn" onClick={onClose}>✕</button>
        </div>

        <form onSubmit={handleSubmit} className="modal-body">
          <p className="modal-section-label">Pickup Details</p>

          <label className="modal-field">
            <span>Pickup Address *</span>
            <input
              name="pickupAddress"
              value={form.pickupAddress}
              onChange={handleChange}
              placeholder="Where should we pick up your laundry?"
              required
            />
          </label>

          <div className="modal-field-row">
            <label className="modal-field">
              <span>Pickup Date *</span>
              <input
                type="date"
                name="scheduledDate"
                value={form.scheduledDate}
                onChange={handleChange}
                min={getMinPickupDate()}
                required
              />
            </label>
            <label className="modal-field">
              <span>Time Slot *</span>
              <select name="timeSlot" value={form.timeSlot} onChange={handleChange}>
                {TIME_SLOTS.map((slot) => (
                  <option key={slot} value={slot}>{slot}</option>
                ))}
              </select>
            </label>
          </div>

          <label className="modal-field">
            <span>Pickup Notes (optional)</span>
            <input
              name="pickupNotes"
              value={form.pickupNotes}
              onChange={handleChange}
              placeholder="Gate code, landmark, etc."
            />
          </label>

          <p className="modal-section-label">Order Details</p>

          <div className="modal-field-row">
            <label className="modal-field">
              <span>Service Type *</span>
              <select name="serviceType" value={form.serviceType} onChange={handleChange}>
                <option value="WASH_FOLD">Wash & Fold</option>
                <option value="DRY_CLEAN">Dry Clean</option>
                <option value="FOLD_ONLY">Fold Only</option>
              </select>
            </label>
            <label className="modal-field">
              <span>Estimated Weight (kg) *</span>
              <input
                type="number"
                name="weightKg"
                value={form.weightKg}
                onChange={handleChange}
                min="0.1"
                max="50"
                step="0.1"
                placeholder="e.g. 5.0"
                required
              />
            </label>
          </div>

          <label className="modal-field">
            <span>Special Instructions (optional)</span>
            <textarea
              name="specialInstructions"
              value={form.specialInstructions}
              onChange={handleChange}
              placeholder="Any special handling instructions?"
              rows={3}
            />
          </label>

          {error && <p className="modal-error">{error}</p>}

          <div className="modal-actions">
            <button type="button" className="btn-ghost" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? "Placing order..." : "Place Order"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function CustomerDashboard({
  user,
  orders,
  loadingOrders,
  fetchError,
  showNewOrderModal,
  onOpenNewOrder,
  onCloseNewOrder,
  onOrderCreated,
}) {
  const activeOrders = orders.filter((o) => o.status !== "Delivered");
  const latestOrder = activeOrders[0];
  const totalOrders = orders.length;
  const deliveredCount = orders.filter((o) => o.status === "Delivered").length;
  const totalSpent = orders.filter((o) => o.paid).reduce((sum, o) => sum + o.price, 0);

  return (
    <>
      {showNewOrderModal && (
        <NewOrderModal
          userId={user.userId}
          onClose={onCloseNewOrder}
          onCreated={onOrderCreated}
        />
      )}

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
              <button className="quick-action-btn" onClick={onOpenNewOrder}>
                <div className="action-icon" style={{ background: "var(--accent-subtle)", color: "var(--accent-primary)" }}>📅</div>
                <span className="action-label">Schedule Pickup</span>
                <span className="action-desc">Book a pickup date</span>
              </button>
              <button className="quick-action-btn" onClick={onOpenNewOrder}>
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
              {loadingOrders ? (
                <tr><td colSpan={7} style={{ textAlign: "center", padding: "24px", color: "var(--text-muted)" }}>Loading your orders...</td></tr>
              ) : fetchError ? (
                <tr><td colSpan={7} style={{ textAlign: "center", padding: "24px", color: "var(--status-unpaid)" }}>{fetchError}</td></tr>
              ) : orders.length === 0 ? (
                <tr><td colSpan={7} style={{ textAlign: "center", padding: "24px", color: "var(--text-muted)" }}>No orders yet. Click &ldquo;New Order&rdquo; to get started.</td></tr>
              ) : (
                orders.map((order) => (
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
                ))
              )}
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