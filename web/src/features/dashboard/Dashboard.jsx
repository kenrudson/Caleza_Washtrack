import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { fetchCustomerOrders, fetchAllOrdersForStaff, advanceOrderStatus, markOrderAsPaid } from "./dashboardService";
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

  const [showNotifications, setShowNotifications] = useState(false);

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
      .catch((err) =>
        setStaffFetchError(err.response?.data?.message || "Could not update that order's status. Please try again.")
      );
  };

  // Interim lightweight payment marking, persisted via the backend (see
  // dashboardService.markOrderAsPaid / backend StaffOrderService for scope note).
  const handleRecordPayment = (orderId) => {
    markOrderAsPaid(orderId)
      .then(() => loadStaffOrders())
      .catch(() => setStaffFetchError("Could not record that payment. Please try again."));
  };

  const unreadCount = MOCK_NOTIFICATIONS.filter((n) => !n.read).length;

  return (
    <div className="dashboard-shell">
      {/* ── Main Content ──────────────────────────── */}
      <main className="main-content">
        {/* Top Header */}
        <header className="top-header">
          <div className="header-left">
            <div className="header-brand">
              <div className="brand-icon">🫧</div>
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

            <div className="header-user">
              <div className="user-avatar">{getInitials(user.fullName || "User")}</div>
              <div className="user-info">
                <span className="user-name">{user.fullName || "User"}</span>
                <span className="user-role">{user.role || "CUSTOMER"}</span>
              </div>
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

