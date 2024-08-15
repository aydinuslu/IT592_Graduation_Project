import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: localStorage.getItem('token') || '',
  }),

  actions: {
    async fetchUser() {
      try {
        if (!this.token) {
          console.error("Token is missing.");
          throw new Error("User is not authenticated.");
        }

        console.log("Fetching user data with token:", this.token);

        const response = await fetch('http://localhost:8081/api/users/me', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`,
          },
        });

        if (!response.ok) {
          console.error("Failed to fetch user data:", response.status);
          throw new Error("Failed to load user data");
        }

        const data = await response.json();
        if (!data) {
          console.error("Empty response received");
          throw new Error("Failed to load user data");
        }

        this.userInfo = data;
        console.log("User data fetched successfully:", this.userInfo);
      } catch (error) {
        console.error("Error fetching user data:", error.message);
        throw error;
      }
    },

    async updateUser(userData) {
      try {
        // Ensure username is included in the update
        userData.username = this.userInfo.username;

        console.log("Updating user data:", userData);

        const response = await fetch(`http://localhost:8081/api/users/${this.userInfo.id}`, {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${this.token}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(userData),
        });

        if (!response.ok) {
          console.error("Failed to update user:", response.status);
          throw new Error("Failed to update user");
        }

        // Update local userInfo with new data
        this.userInfo = { ...this.userInfo, ...userData };
        console.log("User data updated successfully.");
      } catch (error) {
        console.error("Error updating user data:", error.message);
        throw error;
      }
    },
  },
});
