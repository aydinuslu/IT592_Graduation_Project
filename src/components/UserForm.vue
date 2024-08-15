<template>
  <div class="min-h-full flex flex-col justify-center py-12 sm:px-6 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-md">
      <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">Update Profile</h2>
    </div>

    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
        <div v-if="userStore.userInfo">
          <form @submit.prevent="updateProfile" class="space-y-6">
            <div>
              <label for="username" class="block text-sm font-medium text-gray-700">Username</label>
              <div class="mt-1">
                <input
                  type="text"
                  id="username"
                  v-model="userStore.userInfo.username"
                  disabled
                  class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm bg-gray-100 cursor-not-allowed"
                />
              </div>
            </div>

            <div>
              <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
              <div class="mt-1">
                <input
                  type="email"
                  id="email"
                  v-model="userStore.userInfo.email"
                  required
                  class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
            </div>

            <div>
              <label for="password" class="block text-sm font-medium text-gray-700">New Password</label>
              <div class="mt-1">
                <input
                  type="password"
                  id="password"
                  v-model="password"
                  required
                  class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
            </div>

            <div>
              <label for="confirmPassword" class="block text-sm font-medium text-gray-700">Confirm Password</label>
              <div class="mt-1">
                <input
                  type="password"
                  id="confirmPassword"
                  v-model="confirmPassword"
                  required
                  class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                />
              </div>
            </div>

            <div>
              <button
                type="submit"
                class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Update
              </button>
            </div>
          </form>

          <p v-if="updateMessage" class="mt-4 text-center text-green-600">{{ updateMessage }}</p>
        </div>

        <div v-else>
          <p class="text-center text-gray-500">Loading user data...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '../stores/userStore';

const userStore = useUserStore();
const password = ref('');
const confirmPassword = ref('');
const updateMessage = ref('');

onMounted(() => {
  if (!userStore.userInfo) {
    userStore.fetchUser().catch(() => {
      updateMessage.value = 'Failed to load user data. Please try again.';
    });
  }
});

const updateProfile = async () => {
  if (password.value !== confirmPassword.value) {
    updateMessage.value = 'Passwords do not match.';
    return;
  }

  try {
    await userStore.updateUser({
      email: userStore.userInfo.email,
      password: password.value,
    });
    updateMessage.value = 'Profile updated successfully';
  } catch (error) {
    updateMessage.value = 'Failed to update profile. Please try again.';
  }
};
</script>

<style scoped>
/* No additional styles are needed as TailwindCSS handles the styling */
</style>
