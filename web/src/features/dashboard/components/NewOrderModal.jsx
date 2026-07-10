import { useState } from "react";
import { submitNewOrder } from "../dashboardService";
import { TIME_SLOTS } from "../dashboardConstants";
import { getMinPickupDate } from "../dashboardHelpers";

// ─── New Order Modal (FR-004 + FR-005) ───────────────────────
export default function NewOrderModal({ userId, onClose, onCreated }) {
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
      const data = await submitNewOrder({
        userId,
        pickupAddress: form.pickupAddress,
        scheduledDate: form.scheduledDate,
        timeSlot: form.timeSlot,
        pickupNotes: form.pickupNotes,
        serviceType: form.serviceType,
        weightKg: weight,
        specialInstructions: form.specialInstructions,
      });
      onCreated(data);
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

