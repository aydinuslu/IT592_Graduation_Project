<template>
    <div v-if="isVisible" class="fixed bottom-4 right-4 bg-green-500 text-white p-3 rounded-lg shadow-lg transition-opacity duration-300 ease-in-out" :class="{'opacity-0': !isVisible}">
      {{ message }}
    </div>
  </template>
  
  <script setup>
  import { ref, watch, onMounted } from 'vue';
  
  const props = defineProps({
    message: {
      type: String,
      required: true,
    },
    duration: {
      type: Number,
      default: 3000, // default duration of 3 seconds
    },
  });
  
  const isVisible = ref(false);
  
  const showToast = () => {
    isVisible.value = true;
    setTimeout(() => {
      isVisible.value = false;
    }, props.duration);
  };
  
  // Watch for changes in the message prop to trigger the toast
  watch(() => props.message, (newMessage) => {
    if (newMessage) {
      showToast();
    }
  });
  
  onMounted(() => {
    if (props.message) {
      showToast();
    }
  });
  </script>
  
  <style scoped>
  /* Additional styling if needed */
  </style>
  