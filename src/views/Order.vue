<template>
  <div>
    <h2>Order List</h2>
    <ul>
      <li v-for="order in orders" :key="order.id">
        <p>Order ID: {{ order.id }}</p>
        <p>Total: {{ order.total }}</p>
        <ul>
          <li v-for="item in order.items" :key="item.book.id">
            <p>Book: {{ item.book.title }}</p>
            <p>Quantity: {{ item.quantity }}</p>
          </li>
        </ul>
      </li>
    </ul>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/authStore';

export default {
  setup() {
    const authStore = useAuthStore();
    const orders = ref([]);

    onMounted(async () => {
      try {
        const response = await fetch(`${import.meta.env.VITE_ORDER_SERVICE_API}/${authStore.user.id}`);
        orders.value = await response.json();
      } catch (error) {
        console.error('Error fetching orders:', error);
      }
    });

    return {
      orders,
    };
  },
};
</script>
