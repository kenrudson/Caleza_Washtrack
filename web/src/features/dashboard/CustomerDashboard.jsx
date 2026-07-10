import NewOrderModal from "./components/NewOrderModal";
import { SERVICE_ICONS, STATUS_CLASS, STATUS_STEPS } from "./dashboardConstants";
import { getStatusIndex } from "./dashboardHelpers";

export default function CustomerDashboard({
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

