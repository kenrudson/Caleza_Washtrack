import { SERVICE_ICONS, STATUS_CLASS } from "./dashboardConstants";
import { getNextStatus, getTimeGreeting } from "./dashboardHelpers";

// ─── Staff Dashboard ────────────────────────────────────────
export default function StaffDashboard({ user, orders, loadingOrders, fetchError, onStatusUpdate, onRecordPayment, onOpenProfile }) {
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
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", flexWrap: "wrap", gap: "12px" }}>
          <div>
            <h1>Good {getTimeGreeting()}, {user.fullName?.split(" ")[0] || "Staff"}! 💼</h1>
            <p>
              You have {pendingCount} pending order{pendingCount !== 1 ? "s" : ""} and {unpaidCount} unpaid order{unpaidCount !== 1 ? "s" : ""} awaiting action.
            </p>
          </div>
          
        </div>
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
                    <div className="queue-details" style={{ marginTop: "2px" }}>
                      📍 {order.address}
                    </div>
                  </div>
                  <span className={`status-badge ${STATUS_CLASS[order.status]}`}>{order.status}</span>
                  <div className="queue-actions">
                    {getNextStatus(order.status) && (
                      <button className="btn-primary" onClick={() => onStatusUpdate(order.orderId)}>
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
                    <button className="btn-primary" style={{ fontSize: "0.72rem", padding: "5px 10px" }} onClick={() => onRecordPayment(order.orderId)}>
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
                <th>Address</th>
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
                <tr><td colSpan={9} style={{ textAlign: "center", padding: "24px", color: "var(--text-muted)" }}>Loading order queue...</td></tr>
              ) : fetchError ? (
                <tr><td colSpan={9} style={{ textAlign: "center", padding: "24px", color: "var(--status-unpaid)" }}>{fetchError}</td></tr>
              ) : orders.length === 0 ? (
                <tr><td colSpan={9} style={{ textAlign: "center", padding: "24px", color: "var(--text-muted)" }}>No orders yet.</td></tr>
              ) : (
                orders.map((order) => (
                <tr key={order.id}>
                  <td><span className="order-id">{order.id}</span></td>
                  <td>{order.customer}</td>
                  <td style={{ color: "var(--text-secondary)", fontSize: "0.82rem", maxWidth: "180px" }}>{order.address}</td>
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

