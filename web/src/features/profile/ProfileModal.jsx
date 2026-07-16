import { useState, useEffect } from "react";
import { getProfile, updateProfile, changePassword } from "./profileService";
import { getInitials } from "../dashboard/dashboardHelpers";

// ─── Profile Modal (FR-003) ───────────────────────────────────
export default function ProfileModal({ userId, onClose, onProfileSaved }) {
  const [activeTab, setActiveTab] = useState("info"); // "info" | "password"

  // Personal Info tab state
  const [profile, setProfile] = useState(null);
  const [infoForm, setInfoForm] = useState({ fullName: "", phone: "", address: "" });
  const [infoLoading, setInfoLoading] = useState(true);
  const [infoSaving, setInfoSaving] = useState(false);
  const [infoError, setInfoError] = useState("");
  const [infoSuccess, setInfoSuccess] = useState("");

  // Change Password tab state
  const [pwForm, setPwForm] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });
  const [pwSaving, setPwSaving] = useState(false);
  const [pwError, setPwError] = useState("");
  const [pwSuccess, setPwSuccess] = useState("");

  // Load profile on mount
  useEffect(() => {
    getProfile(userId)
      .then((data) => {
        setProfile(data);
        setInfoForm({
          fullName: data.fullName || "",
          phone: data.phone || "",
          address: data.address || "",
        });
      })
      .catch(() => setInfoError("Could not load your profile. Please try again."))
      .finally(() => setInfoLoading(false));
  }, [userId]);

  const handleInfoChange = (e) =>
    setInfoForm({ ...infoForm, [e.target.name]: e.target.value });

  const handleInfoSubmit = async (e) => {
    e.preventDefault();
    setInfoError("");
    setInfoSuccess("");
    setInfoSaving(true);
    try {
      const updated = await updateProfile(userId, infoForm);
      setProfile(updated);
      setInfoSuccess("Profile updated successfully!");
      // Refresh the cached user in localStorage so the header reflects changes immediately
      const cached = JSON.parse(localStorage.getItem("user") || "{}");
      localStorage.setItem(
        "user",
        JSON.stringify({ ...cached, fullName: updated.fullName })
      );
      onProfileSaved(updated);
    } catch (err) {
      setInfoError(err.response?.data?.message || "Could not update profile. Please try again.");
    } finally {
      setInfoSaving(false);
    }
  };

  const handlePwChange = (e) =>
    setPwForm({ ...pwForm, [e.target.name]: e.target.value });

  const handlePwSubmit = async (e) => {
    e.preventDefault();
    setPwError("");
    setPwSuccess("");
    if (pwForm.newPassword !== pwForm.confirmPassword) {
      setPwError("New passwords do not match.");
      return;
    }
    setPwSaving(true);
    try {
      await changePassword(userId, {
        currentPassword: pwForm.currentPassword,
        newPassword: pwForm.newPassword,
      });
      setPwSuccess("Password changed successfully!");
      setPwForm({ currentPassword: "", newPassword: "", confirmPassword: "" });
    } catch (err) {
      setPwError(err.response?.data?.message || "Could not change password. Please try again.");
    } finally {
      setPwSaving(false);
    }
  };

  const memberSince = profile?.createdAt
    ? new Date(profile.createdAt).toLocaleDateString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
      })
    : "—";

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div
        className="modal-card profile-modal-card"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="modal-header">
          <h3>My Profile</h3>
          <button className="modal-close-btn" onClick={onClose}>✕</button>
        </div>

        {/* Avatar + identity summary */}
        <div className="profile-hero">
          <div className="profile-avatar-lg">
            {profile ? getInitials(profile.fullName || "User") : "…"}
          </div>
          <div className="profile-hero-info">
            <div className="profile-hero-name">
              {profile?.fullName || "—"}
            </div>
            <div className="profile-hero-email">{profile?.email || "—"}</div>
            <div className="profile-hero-meta">
              <span className="profile-role-badge">{profile?.role || "CUSTOMER"}</span>
              <span className="profile-member-since">Member since {memberSince}</span>
            </div>
          </div>
        </div>

        {/* Tab switcher */}
        <div className="profile-tabs">
          <button
            className={`profile-tab-btn ${activeTab === "info" ? "active" : ""}`}
            onClick={() => { setActiveTab("info"); setInfoError(""); setInfoSuccess(""); }}
          >
            👤 Personal Info
          </button>
          <button
            className={`profile-tab-btn ${activeTab === "password" ? "active" : ""}`}
            onClick={() => { setActiveTab("password"); setPwError(""); setPwSuccess(""); }}
          >
            🔒 Change Password
          </button>
        </div>

        {/* ── Personal Info Tab ── */}
        {activeTab === "info" && (
          <div className="modal-body">
            {infoLoading ? (
              <div className="profile-loading">Loading your profile…</div>
            ) : (
              <form onSubmit={handleInfoSubmit}>
                {/* Email — read-only */}
                <label className="modal-field">
                  <span>Email (read-only)</span>
                  <input
                    type="email"
                    value={profile?.email || ""}
                    readOnly
                    className="profile-readonly-input"
                  />
                </label>

                <label className="modal-field">
                  <span>Full Name *</span>
                  <input
                    name="fullName"
                    value={infoForm.fullName}
                    onChange={handleInfoChange}
                    placeholder="Your full name"
                    required
                  />
                </label>

                <label className="modal-field">
                  <span>Phone Number *</span>
                  <input
                    name="phone"
                    value={infoForm.phone}
                    onChange={handleInfoChange}
                    placeholder="e.g. 09171234567"
                    required
                  />
                </label>

                <label className="modal-field">
                  <span>Address *</span>
                  <input
                    name="address"
                    value={infoForm.address}
                    onChange={handleInfoChange}
                    placeholder="Your home / delivery address"
                    required
                  />
                </label>

                {infoError && <p className="modal-error">{infoError}</p>}
                {infoSuccess && <p className="modal-success">{infoSuccess}</p>}

                <div className="modal-actions">
                  <button type="button" className="btn-ghost" onClick={onClose}>
                    Cancel
                  </button>
                  <button type="submit" className="btn-primary" disabled={infoSaving}>
                    {infoSaving ? "Saving…" : "Save Changes"}
                  </button>
                </div>
              </form>
            )}
          </div>
        )}

        {/* ── Change Password Tab ── */}
        {activeTab === "password" && (
          <div className="modal-body">
            <form onSubmit={handlePwSubmit}>
              <label className="modal-field">
                <span>Current Password *</span>
                <input
                  type="password"
                  name="currentPassword"
                  value={pwForm.currentPassword}
                  onChange={handlePwChange}
                  placeholder="Your current password"
                  required
                />
              </label>

              <label className="modal-field">
                <span>New Password *</span>
                <input
                  type="password"
                  name="newPassword"
                  value={pwForm.newPassword}
                  onChange={handlePwChange}
                  placeholder="At least 8 characters"
                  minLength={8}
                  required
                />
              </label>

              <label className="modal-field">
                <span>Confirm New Password *</span>
                <input
                  type="password"
                  name="confirmPassword"
                  value={pwForm.confirmPassword}
                  onChange={handlePwChange}
                  placeholder="Repeat new password"
                  required
                />
              </label>

              {pwError && <p className="modal-error">{pwError}</p>}
              {pwSuccess && <p className="modal-success">{pwSuccess}</p>}

              <div className="modal-actions">
                <button type="button" className="btn-ghost" onClick={onClose}>
                  Cancel
                </button>
                <button type="submit" className="btn-primary" disabled={pwSaving}>
                  {pwSaving ? "Updating…" : "Change Password"}
                </button>
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
