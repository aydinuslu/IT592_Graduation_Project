import { defineStore } from "pinia";
import { decodeJwt } from "jose"; // Decoding JWT tokens

const API_URL = 'http://localhost:8081/api/users';

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: null,
    user: null,
  }),
  actions: {
    async register(userData) {
      const response = await fetch(`${API_URL}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          ...userData,
          role: "User", // Force role to User
        }),
      });

      if (!response.ok) {
        throw new Error("Registration failed");
      }

      const data = await response.json();
      return data;
    },

    async login(credentials) {
      const response = await fetch(`${API_URL}/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Login failed: ${errorText}`);
      }

      let data;
      try {
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
          data = await response.json();
        } else {
          data = await response.text();
        }
      } catch (e) {
        throw new Error("Invalid response format");
      }

      if (data.token) {
        this.token = data.token;
        this.user = decodeJwt(data.token); // Use jose to decode token
        localStorage.setItem("token", this.token);
        localStorage.setItem("user", JSON.stringify(this.user));
      } else if (data.message) {
        throw new Error(data.message);
      } else {
        throw new Error("Invalid login response");
      }
    },

    logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
    },

    async checkAuth() {
      const token = localStorage.getItem("token");
      const user = localStorage.getItem("user");

      if (token && user) {
        this.token = token;
        this.user = JSON.parse(user);
      }
    },

    // Helper to get Authorization header
    getAuthHeader() {
      return this.token ? { 'Authorization': `Bearer ${this.token}` } : {};
    }
  },
});
