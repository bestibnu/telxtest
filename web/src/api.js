const API_BASE = "http://localhost:8080/api";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json"
    },
    ...options
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed: ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export function requestOtp(phone) {
  return request("/auth/request-otp", {
    method: "POST",
    body: JSON.stringify({ phone })
  });
}

export function verifyOtp(phone, code) {
  return request("/auth/verify-otp", {
    method: "POST",
    body: JSON.stringify({ phone, code })
  });
}

export function fetchContacts() {
  return request("/contacts");
}

export function addContact(name, phone) {
  return request("/contacts", {
    method: "POST",
    body: JSON.stringify({ name, phone })
  });
}

export function deleteContact(id) {
  return request(`/contacts/${id}`, {
    method: "DELETE"
  });
}

export function fetchCalls() {
  return request("/calls");
}

export function placeCall(from, to) {
  return request("/calls", {
    method: "POST",
    body: JSON.stringify({ from, to })
  });
}

export function fetchCredits() {
  return request("/credits");
}

export function topUpCredits(amount) {
  return request("/credits/topup", {
    method: "POST",
    body: JSON.stringify({ amount })
  });
}
