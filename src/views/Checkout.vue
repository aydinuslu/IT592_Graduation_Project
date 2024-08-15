<template>
    <div class="container mx-auto">
      <h1 class="text-center text-3xl font-bold my-8">Checkout</h1>
      <div v-if="cart.length === 0">
        <p>Your cart is empty.</p>
      </div>
      <div v-else>
        <div v-for="item in cart" :key="item.id" class="cart-item flex justify-between items-center mb-4">
          <img :src="item.coverImage || 'https://picsum.photos/350/300?image=1073'" alt="Book Cover" class="w-20 h-20 object-cover mr-4">
          <div class="flex-1">
            <h2 class="text-xl font-semibold">{{ item.title }}</h2>
            <p class="text-gray-600">Author: {{ item.author }}</p>
            <p class="text-gray-800 font-bold">Price: ${{ item.price }}</p>
            <p class="text-gray-800">Quantity: {{ item.quantity }}</p>
          </div>
        </div>
        <div class="text-right font-bold text-xl mt-4">
          Total: ${{ cartTotalPrice.toFixed(2) }}
        </div>
        <button @click="placeOrder" class="bg-blue-500 text-white px-4 py-2 rounded mt-4 hover:bg-blue-600">Place Order</button>
      </div>
    </div>
  </template>
  
  <script>
  import { computed } from 'vue';
  import { useBookStore } from '@/stores/bookStore';
  
  export default {
    setup() {
      const bookStore = useBookStore();
  
      const cart = computed(() => bookStore.cart);
      const cartTotalPrice = computed(() => bookStore.cartTotalPrice);
  
      const placeOrder = () => {
        // Implement order placement logic here
        alert('Order placed successfully!');
        // Clear the cart after placing the order
        bookStore.cart = [];
      };
  
      return {
        cart,
        cartTotalPrice,
        placeOrder,
      };
    },
  };
  </script>
  
  <style scoped>
  .container {
    padding: 20px;
    max-width: 800px;
  }
  
  .cart-item {
    border-bottom: 1px solid #ccc;
    padding-bottom: 10px;
  }
  </style>
  