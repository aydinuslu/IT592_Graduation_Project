<template>
  <div class="container mx-auto px-4">
    <h1 class="text-center text-3xl font-bold my-8">Checkout</h1>
    <div v-if="cartItems && cartItems.length > 0">
      <div
        v-for="item in cartItems"
        :key="item.bookId"
        class="flex items-center justify-between p-4 bg-white shadow-md rounded-lg mb-4"
      >
        <div>
          <h2 class="text-xl font-semibold">{{ item.book.title }}</h2>
          <p class="text-gray-600">Author: {{ item.book.author }}</p>
          <p class="text-gray-800 font-bold">Price: ${{ item.book.price }}</p>
          <p>Quantity: {{ item.quantity }}</p>
        </div>
      </div>
      <div class="text-right text-2xl font-bold mt-4">
        Total: ${{ totalPrice.toFixed(2) }}
      </div>
      <button
        @click="submitOrder"
        class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 mt-4"
      >
        Submit Order
      </button>
    </div>
    <div v-else>
      <p class="text-center text-gray-600">Your cart is empty.</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useShoppingCartStore } from '@/stores/shoppingCartStore';
import { useOrderStore } from '@/stores/orderStore';
import { useAuthStore } from '@/stores/authStore';

const shoppingCartStore = useShoppingCartStore();
const orderStore = useOrderStore();
const authStore = useAuthStore();

const cartItems = computed(() => shoppingCartStore.cart.items);

const totalPrice = computed(() =>
  cartItems.value.reduce((total, item) => {
    return total + (item.book.price * item.quantity);
  }, 0)
);

async function submitOrder() {
  try {
    const userId = authStore.user?.id || localStorage.getItem('userId');

    if (!userId) {
      console.error('User ID not found, unable to submit order.');
      return;
    }

    const orderData = cartItems.value.map(item => ({
      bookId: item.book.id,
      quantity: item.quantity,
      price: item.book.price,
    }));

    console.log('Submitting order for user ID:', userId);
    console.log('Order Data:', orderData);

    await orderStore.createOrder(orderData);

    console.log('Order successfully placed');
  } catch (error) {
    console.error('Error placing order:', error);
  }
}
</script>
