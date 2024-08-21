import { defineStore } from "pinia";
import { decodeJwt } from "jose"; // Decoding JWT tokens
import { useShoppingCartStore } from '@/stores/shoppingCartStore'; // Import shoppingCartStore

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
        data = await response.json();
      } catch (e) {
        throw new Error("Invalid response format");
      }

      if (data.token) {
        this.token = data.token;
        this.user = decodeJwt(data.token); // Decoding the token

        // Extract the username (sub) from the JWT
        const username = this.user.sub;

        // Fetch all users to find the userId for the logged-in user
        const usersResponse = await fetch(`${API_URL}`, {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${this.token}`,
          }
        });

        if (!usersResponse.ok) {
          throw new Error("Failed to retrieve users");
        }

        const usersData = await usersResponse.json();
        const user = usersData.find(u => u.username === username);

        if (!user || !user.id) {
          throw new Error("User ID not found");
        }

        const userId = user.id;

        // Store the token, user details, and userId
        localStorage.setItem("token", this.token);
        localStorage.setItem("user", JSON.stringify(this.user));
        localStorage.setItem("userId", userId); // Store userId separately

        // After successful login, merge the local cart with the remote cart
        await this.mergeLocalCartWithRemoteCart(userId);  // Pass userId to the merge function
      } else if (data.message) {
        throw new Error(data.message);
      } else {
        throw new Error("Invalid login response");
      }
    },

    async mergeLocalCartWithRemoteCart(userId) {
      const localCart = JSON.parse(localStorage.getItem('cartItems')) || []; // Corrected local cart key
      const shoppingCartStore = useShoppingCartStore();

      if (localCart.length > 0) {
        for (let localItem of localCart) {
          await shoppingCartStore.addItemToCart(userId, localItem);  // Add items from local to remote
        }
        localStorage.removeItem('cartItems'); // Clear local cart after merging
      }
    },

    logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      localStorage.removeItem("userId");
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
