<template>
  <div class="bg-white shadow-lg rounded-lg overflow-hidden">
    <img :src="book.coverImage || 'https://picsum.photos/350/300?image=1073'" alt="Book Cover" class="w-full h-48 object-cover">
    <div class="p-4">
      <h2 class="text-xl font-semibold">{{ book.title }}</h2>
      <p class="text-gray-600">{{ book.author }}</p>
      <p class="text-gray-800 font-bold">${{ book.price }}</p>
      <div class="flex justify-between mt-4">
        <router-link :to="`/books/edit/${book.id}`" class="text-blue-500 hover:underline">View Details</router-link>
        <button @click="addToCart(book)" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">Add to Cart</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { useShoppingCartStore } from '@/stores/shoppingCartStore';
import { useRouter } from 'vue-router';

const shoppingCartStore = useShoppingCartStore();
const authStore = useAuthStore();
const router = useRouter();

async function addToCart(book) {
  if (authStore.user) {
    try {
      const userId = localStorage.getItem('userId');
      if (userId) {
        await shoppingCartStore.addItemToCart(userId, book);
        console.log("Item added to remote cart");
      }
    } catch (error) {
      console.error("Error adding to cart:", error);
    }
  } else {
    router.push('/login');
  }
}
</script>
