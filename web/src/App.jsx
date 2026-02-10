import React, { useEffect, useState } from "react";
import {
  addContact,
  deleteContact,
  fetchCalls,
  fetchContacts,
  fetchCredits,
  placeCall,
  requestOtp,
  topUpCredits,
  verifyOtp
} from "./api.js";

export default function App() {
  const [phone, setPhone] = useState("");
  const [code, setCode] = useState("");
  const [token, setToken] = useState("");
  const [requestId, setRequestId] = useState("");
  const [status, setStatus] = useState("");
  const [contacts, setContacts] = useState([]);
  const [calls, setCalls] = useState([]);
  const [credits, setCredits] = useState({ balance: "0.00", currency: "USD" });
  const [newContact, setNewContact] = useState({ name: "", phone: "" });
  const [callTo, setCallTo] = useState("");
  const [topUpAmount, setTopUpAmount] = useState("5.00");

  useEffect(() => {
    if (!token) return;
    refreshData();
  }, [token]);

  async function refreshData() {
    try {
      const [contactData, callData, creditData] = await Promise.all([
        fetchContacts(),
        fetchCalls(),
        fetchCredits()
      ]);
      setContacts(contactData);
      setCalls(callData);
      setCredits(creditData);
    } catch (error) {
      setStatus(error.message);
    }
  }

  async function handleRequestOtp() {
    setStatus("");
    try {
      const response = await requestOtp(phone);
      setRequestId(response.requestId);
      setStatus("OTP sent. Use code 0000 for the stub flow.");
    } catch (error) {
      setStatus(error.message);
    }
  }

  async function handleVerifyOtp() {
    setStatus("");
    try {
      const response = await verifyOtp(phone, code);
      setToken(response.token);
      setStatus("Signed in.");
      if (window.TelxNative?.onLoggedIn) {
        window.TelxNative.onLoggedIn();
      }
    } catch (error) {
      setStatus("OTP invalid. Try 0000 for now.");
    }
  }

  async function handleAddContact() {
    if (!newContact.name || !newContact.phone) {
      setStatus("Enter a name and phone number.");
      return;
    }
    try {
      const created = await addContact(newContact.name, newContact.phone);
      setContacts((prev) => [created, ...prev]);
      setNewContact({ name: "", phone: "" });
    } catch (error) {
      setStatus(error.message);
    }
  }

  async function handleDeleteContact(id) {
    try {
      await deleteContact(id);
      setContacts((prev) => prev.filter((contact) => contact.id !== id));
    } catch (error) {
      setStatus(error.message);
    }
  }

  async function handlePlaceCall(target) {
    if (!phone || !target) {
      setStatus("Enter your number and a destination.");
      return;
    }
    try {
      const record = await placeCall(phone, target);
      setCalls((prev) => [record, ...prev]);
      setCallTo("");
    } catch (error) {
      setStatus(error.message);
    }
  }

  async function handleTopUp() {
    try {
      const updated = await topUpCredits(topUpAmount);
      setCredits(updated);
    } catch (error) {
      setStatus(error.message);
    }
  }

  return (
    <div className="app">
      <header className="hero">
        <div>
          <p className="eyebrow">TelxTest MVP</p>
          <h1>Global calling, simplified.</h1>
          <p className="lead">
            A Rebtel-inspired experience with wallet credits, quick dial, and recent
            calls. This MVP uses stubbed calling flows while we wire a provider.
          </p>
        </div>
        <div className="status-card">
          <div>
            <p className="label">Credits</p>
            <p className="value">
              {credits.balance} {credits.currency}
            </p>
          </div>
          <div>
            <p className="label">Status</p>
            <p className="value small">{status || "Ready."}</p>
          </div>
        </div>
      </header>

      {!token ? (
        <section className="panel">
          <h2>Sign in with your phone</h2>
          <div className="form-row">
            <input
              placeholder="Phone number"
              value={phone}
              onChange={(event) => setPhone(event.target.value)}
            />
            <button onClick={handleRequestOtp}>Request OTP</button>
          </div>
          <div className="form-row">
            <input
              placeholder="OTP code"
              value={code}
              onChange={(event) => setCode(event.target.value)}
            />
            <button onClick={handleVerifyOtp}>Verify</button>
          </div>
          {requestId && <p className="hint">Request id: {requestId}</p>}
        </section>
      ) : (
        <section className="grid">
          <div className="panel">
            <h2>Quick dial</h2>
            <div className="form-row">
              <input
                placeholder="Destination number"
                value={callTo}
                onChange={(event) => setCallTo(event.target.value)}
              />
              <button onClick={() => handlePlaceCall(callTo)}>Place call</button>
            </div>
            <p className="hint">Calling is stubbed for now. Records are saved.</p>
          </div>

          <div className="panel">
            <h2>Top up credits</h2>
            <div className="form-row">
              <input
                value={topUpAmount}
                onChange={(event) => setTopUpAmount(event.target.value)}
              />
              <button onClick={handleTopUp}>Add</button>
            </div>
          </div>

          <div className="panel">
            <h2>Contacts</h2>
            <div className="form-row">
              <input
                placeholder="Name"
                value={newContact.name}
                onChange={(event) =>
                  setNewContact((prev) => ({ ...prev, name: event.target.value }))
                }
              />
              <input
                placeholder="Phone"
                value={newContact.phone}
                onChange={(event) =>
                  setNewContact((prev) => ({ ...prev, phone: event.target.value }))
                }
              />
              <button onClick={handleAddContact}>Add</button>
            </div>
            <div className="list">
              {contacts.map((contact) => (
                <div className="list-item" key={contact.id}>
                  <div>
                    <p className="value">{contact.name}</p>
                    <p className="label">{contact.phone}</p>
                  </div>
                  <div className="actions">
                    <button onClick={() => handlePlaceCall(contact.phone)}>
                      Call
                    </button>
                    <button
                      className="ghost"
                      onClick={() => handleDeleteContact(contact.id)}
                    >
                      Remove
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="panel">
            <h2>Recent calls</h2>
            <div className="list">
              {calls.map((call) => (
                <div className="list-item" key={call.id}>
                  <div>
                    <p className="value">
                      {call.from} → {call.to}
                    </p>
                    <p className="label">
                      {new Date(call.startedAt).toLocaleString()} · {call.status}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>
      )}
    </div>
  );
}
