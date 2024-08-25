<template>
  <nav class="bg-teal-500 p-6">
    <div class="container mx-auto flex justify-between items-center">
      <div class="text-white font-bold text-xl">
        <router-link to="/">Bookstore</router-link>
      </div>
      <div class="space-x-4 flex items-center">
        <!-- Non-Admin Links -->
        <template v-if="!isAdmin">
          <router-link to="/" class="text-white">Home</router-link>
          <router-link to="/books" class="text-white">All Products</router-link>
          <router-link to="/cart" class="text-white">Shopping Cart</router-link>
          <router-link to="/checkout" class="text-white">Checkout</router-link>
        </template>

        <!-- Administrator Links -->
        <template v-if="isAdmin">
          <router-link to="/manage-books" class="text-white">Manage Books</router-link>
          <router-link to="/add-book" class="text-white">Add Book</router-link>
          <router-link to="/view-payments" class="text-white">View Payments</router-link>
        </template>

        <!-- Auth Links -->
        <router-link v-if="!isAuthenticated" to="/login" class="text-white">Login</router-link>
        <router-link v-if="!isAuthenticated" to="/register" class="text-white">Register</router-link>
        <router-link v-if="isAuthenticated" to="/user" class="text-white">Profile</router-link>
        <button v-if="isAuthenticated" @click="handleLogout" class="text-white">Logout</button>
      </div>
    </div>
  </nav>
</template>

<script>
import { computed } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import { useRouter } from 'vue-router';

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();

    const isAuthenticated = computed(() => authStore.user !== null);

    const isAdmin = computed(() => {
      const user = authStore.user || JSON.parse(localStorage.getItem('user'));
      return user && user.role === 'Administrator';
    });

    const handleLogout = () => {
      authStore.logout();
      localStorage.removeItem('user'); // Ensure the user data is also removed from localStorage
      router.push('/'); // Redirect to home after logout
      alert("You've been logged out successfully.");
    };

    return {
      isAuthenticated,
      isAdmin,
      handleLogout,
    };
  }
};
</script>

<style scoped>
/* All styles have been moved inline using Tailwind classes */
</style>
