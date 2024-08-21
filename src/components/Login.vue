<template>
  <div class="flex items-center justify-center h-screen">
    <div class="w-full max-w-md">
      <form @submit.prevent="handleLogin" class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="username">
            Username
          </label>
          <input
            v-model="username"
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="username"
            type="text"
            placeholder="Username"
          />
        </div>
        <div class="mb-6">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="password">
            Password
          </label>
          <input
            v-model="password"
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
            id="password"
            type="password"
            placeholder="******************"
          />
        </div>
        <div class="flex items-center justify-between">
          <button
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
            type="submit"
          >
            Login
          </button>
          <div class="mt-4 text-center">
        <router-link to="/register" class="inline-block align-baseline font-bold text-sm text-blue-500 hover:text-blue-800">Not a user yet? Create a profile</router-link>
      </div>
        </div>
        <div v-if="loginError" class="mt-4 text-red-500">
          {{ loginError }}
        </div>
      </form>
      <p class="text-center text-gray-500 text-xs">
        &copy;2023 Acme Corp. All rights reserved.
      </p>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from "../stores/authStore";

export default {
  data() {
    return {
      username: "",
      password: "",
      loginError: null,
    };
  },
  methods: {
    async handleLogin() {
      const authStore = useAuthStore();
      try {
        await authStore.login({
          username: this.username,
          password: this.password,
        });
        this.$router.push("/");
      } catch (error) {
        this.loginError = error.message;
      }
    },
  },
};
</script>

<style scoped>
/* Tailwind CSS classes are already embedded within the template */
</style>