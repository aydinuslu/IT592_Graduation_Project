<template>
  <div class="container mx-auto p-4">
    <h1 class="text-3xl font-bold mb-6">Shopping Cart</h1>
    <div v-if="cartItems.length > 0" class="space-y-4">
      <div v-for="item in cartItems" :key="item.id" class="flex items-center justify-between bg-white p-4 shadow rounded-lg">
        <div class="flex items-center space-x-4">
          <img :src="item.coverImage || 'https://picsum.photos/350/300?image=1073'" alt="Book Cover" class="w-20 h-20 object-cover">
          <div>
            <h2 class="text-xl font-semibold">{{ item.title }}</h2>
            <p class="text-gray-600">Author: {{ item.author }}</p>
            <p class="text-gray-800 font-bold">Price: ${{ item.price }}</p>
            <div class="flex items-center space-x-2 mt-2">
              <button @click="updateQuantity(item.id, item.quantity - 1)" class="px-2 py-1 bg-gray-300 rounded">-</button>
              <span>{{ item.quantity }}</span>
              <button @click="updateQuantity(item.id, item.quantity + 1)" class="px-2 py-1 bg-gray-300 rounded">+</button>
            </div>
          </div>
        </div>
        <button @click="removeItem(item.id)" class="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600">Remove</button>
      </div>
      <div class="text-right text-2xl font-bold">
        Total: ${{ total }}
      </div>
    </div>
    <div v-else class="text-center text-xl text-gray-600">
      Your cart is empty.
    </div>
  </div>
</template>

<script>
import { computed } from 'vue';
import { useCartStore } from '@/stores/cartStore';

export default {
  setup() {
    const cartStore = useCartStore();

    const cartItems = computed(() => cartStore.items);
    const total = computed(() => cartStore.totalPrice);

    const updateQuantity = (id, quantity) => {
      if (quantity > 0) {
        cartStore.updateItemQuantity(id, quantity);
      }
    };

    const removeItem = (id) => {
      cartStore.removeItemFromCart(id);
    };

    return {
      cartItems,
      total,
      updateQuantity,
      removeItem,
    };
  },
};
</script>
