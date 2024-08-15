<template>
  <nav class="bg-teal-500 p-6">
    <div class="container mx-auto flex justify-between items-center">
      <div class="text-white font-bold text-xl">
        <router-link to="/">Bookstore</router-link>
      </div>
      <div class="space-x-4">
        <router-link to="/" class="text-white">Home</router-link>
        <router-link to="/books" class="text-white">All Products</router-link>
        <router-link to="/cart" class="text-white">Shopping Cart</router-link>
        <router-link to="/checkout" class="text-white">Checkout</router-link>
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

    const handleLogout = () => {
      authStore.logout();
      router.push('/'); // Redirect to home after logout
      alert("You've been logged out successfully.");
    };

    return {
      isAuthenticated,
      handleLogout,
    };
  }
};
</script>

<style scoped>
nav {
  background-color: #319795;
  padding: 1rem;
}
.container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
