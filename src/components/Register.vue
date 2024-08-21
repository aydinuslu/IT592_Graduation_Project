<template>
  <div class="flex items-center justify-center h-screen">
    <div class="w-full max-w-md">
      <form @submit.prevent="handleRegister" class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
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
        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="password">
            Password
          </label>
          <input
            v-model="password"
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="password"
            type="password"
            placeholder="******************"
          />
        </div>
        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2" for="email">
            Email
          </label>
          <input
            v-model="email"
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="email"
            type="email"
            placeholder="Email"
          />
        </div>
        <div class="flex items-center justify-between">
          <button
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
            type="submit"
          >
            Register
          </button>
          <div class="mt-4 text-center">
        <router-link to="/login" class="inline-block align-baseline font-bold text-sm text-blue-500 hover:text-blue-800">Already have an account? Login</router-link>
      </div>
        </div>
        <div v-if="registrationError" class="mt-4 text-red-500">
          {{ registrationError }}
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
      email: "",
      registrationError: null,
    };
  },
  methods: {
    async handleRegister() {
      const authStore = useAuthStore();
      try {
        await authStore.register({
          username: this.username,
          password: this.password,
          email: this.email,
        });
        this.$router.push("/login");
      } catch (error) {
        this.registrationError = error.message;
      }
    },
  },
};
</script>

<style scoped>
/* Tailwind CSS classes are already embedded within the template */
</style>
