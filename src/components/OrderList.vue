<template>
  <div class="max-w-4xl mx-auto p-4">
    <h1 class="text-2xl font-bold mb-4">Order History</h1>
    <ul>
      <li v-for="order in orders" :key="order.id" class="border-b py-2">
        <h2 class="text-xl font-bold">Order #{{ order.id }}</h2>
        <p>{{ order.name }}</p>
        <p>{{ order.address }}</p>
        <p>{{ order.paymentMethod }}</p>
        <ul>
          <li v-for="item in order.items" :key="item.id">
            {{ item.title }} - {{ item.quantity }}
          </li>
        </ul>
      </li>
    </ul>
    <p v-if="error" class="text-red-500">{{ error }}</p>
  </div>
</template>

<script>
import { useOrderStore } from '@/stores/orderStore';
import { onMounted } from 'vue';

export default {
  name: 'OrderList',
  setup() {
    const orderStore = useOrderStore();

    onMounted(async () => {
      await orderStore.fetchOrders();
    });

    return {
      orders: orderStore.orders,
      error: orderStore.error,
    };
  },
};
</script>

<style scoped>
/* Add styles for the OrderList component if needed */
</style>
