import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  fetchCustomerOrders,
  fetchAllOrdersForStaff,
  advanceOrderStatus,
  markOrderAsPaid,
  fetchMyNotifications,
  markNotificationsRead,
} from "./dashboardService";
import BellIcon from "./components/BellIcon";
import CustomerDashboard from "./CustomerDashboard";
import StaffDashboard from "./StaffDashboard";
import ProfileModal from "../profile/ProfileModal";
import { getInitials, toDisplayOrder, toStaffDisplayOrder, toDisplayNotification } from "./dashboardHelpers";
import "./Dashboard.css";

// ─── Main Dashboard Component ───────────────────────────────
export default function Dashboard() {
  const navigate = useNavigate();
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem("user") || "{}"));
  const isStaff = user.role === "STAFF";

  const [showProfileModal, setShowProfileModal] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [notifications, setNotifications] = useState([]);

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

  // FR-010: real notifications — works identically for CUSTOMER and STAFF accounts
  const loadNotifications = () => {
    if (!user.userId) return;
    fetchMyNotifications(user.userId)
      .then((data) => setNotifications(data.map(toDisplayNotification)))
      .catch(() => {
        // Notifications are secondary to core functionality — fail silently
        // rather than blocking the rest of the dashboard on an error banner.
      });
  };

  useEffect(() => {
    if (isStaff) {
      loadStaffOrders();
    } else {
      loadCustomerOrders();
    }
    loadNotifications();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user.userId, isStaff]);

  const handleToggleNotifications = () => {
    const opening = !showNotifications;
    setShowNotifications(opening);
    if (opening && notifications.some((n) => !n.read)) {
      markNotificationsRead(user.userId)
        .then(() => loadNotifications())
        .catch(() => {});
    }
  };

  const handleOrderCreated = (newOrderResponse) => {
    setCustomerOrders((prev) => [toDisplayOrder(newOrderResponse), ...prev]);
    setShowNewOrderModal(false);
    loadNotifications();
  };

  const handleProfileSaved = (updatedProfile) => {
    const refreshed = { ...user, fullName: updatedProfile.fullName };
    setUser(refreshed);
    localStorage.setItem("user", JSON.stringify(refreshed));
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  // FR-007: advance the order to its next status via the real backend, then refresh the queue
  const handleStatusUpdate = (orderId) => {
    advanceOrderStatus(orderId)
      .then(() => {
        loadStaffOrders();
        loadNotifications();
      })
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

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <div className="dashboard-shell">
      {/* ── Profile Modal ─────────────────────────── */}
      {showProfileModal && (
        <ProfileModal
          userId={user.userId}
          onClose={() => setShowProfileModal(false)}
          onProfileSaved={handleProfileSaved}
        />
      )}

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
               
              </div>
            </div>
          </div>

          <div className="header-right">
            <button
              className="profile-icon-btn"
              onClick={() => setShowProfileModal(true)}
              title="My Profile"
            >
              👤 Profile
            </button>

            <div style={{ position: "relative" }}>
              <button
                className="notification-btn"
                onClick={handleToggleNotifications}
                title="Notifications"
              >
                <BellIcon size={20} />
                {unreadCount > 0 && <span className="notif-count">{unreadCount}</span>}
              </button>

              {showNotifications && (
                <div className="notification-dropdown">
                  <div className="dropdown-header">
                    <h3>Notifications</h3>
                    <button
                      className="btn-ghost"
                      style={{ fontSize: "0.75rem" }}
                      onClick={() => markNotificationsRead(user.userId).then(() => loadNotifications())}
                    >
                      Mark all read
                    </button>
                  </div>
                  <div className="dropdown-body">
                    {notifications.length === 0 ? (
                      <div style={{ padding: "20px", textAlign: "center", color: "var(--text-muted)", fontSize: "0.85rem" }}>
                        No notifications yet.
                      </div>
                    ) : (
                      notifications.map((n) => (
                        <div className="notification-item" key={n.id}>
                          <div className={`notif-dot ${n.read ? "read" : ""}`} />
                          <div className="notif-content">
                            <div className="notif-text">{n.text}</div>
                            <div className="notif-time">{n.time}</div>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}
            </div>

            <div
              className="header-user"
              style={{ cursor: "pointer" }}
              onClick={() => setShowProfileModal(true)}
              title="Edit profile"
            >
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
              onOpenProfile={() => setShowProfileModal(true)}
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
              onOpenProfile={() => setShowProfileModal(true)}
            />
          )}
        </div>
      </main>
    </div>
  );
}

