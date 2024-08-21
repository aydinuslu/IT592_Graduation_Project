<template>
  <div class="max-w-xl mx-auto p-6 bg-white rounded-lg shadow-md">
    <h2 class="text-2xl font-bold mb-4">Order Details</h2>
    <div v-if="order">
      <p class="mb-4"><span class="font-semibold">Order ID:</span> {{ order.id }}</p>
      <ul class="mb-4">
        <li v-for="item in order.items" :key="item.id" class="flex justify-between mb-2">
          <span>{{ item.name }}</span>
          <span>{{ item.price }}</span>
        </li>
      </ul>
      <p class="text-lg font-semibold">Total: {{ order.total }}</p>
    </div>
    <div v-else class="text-gray-500">
      Loading order details...
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useOrderStore } from '@/stores/orderStore';
import { useRoute } from 'vue-router';

const orderStore = useOrderStore();
const route = useRoute();
const order = ref(null);

onMounted(async () => {
  const orderId = route.params.id;
  await orderStore.fetchOrderById(orderId);
  order.value = orderStore.currentOrder;
});
</script>
