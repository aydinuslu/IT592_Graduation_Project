<template>
  <div class="bg-white shadow-lg rounded-lg overflow-hidden max-w-sm">
    <img :src="book.coverImage || fallbackImage" alt="Book Cover" class="w-full h-48 object-cover">
    <div class="p-4">
      <h2 class="text-xl font-semibold">{{ book.title }}</h2>
      <p class="text-gray-600">{{ book.author }}</p>
      <p class="text-gray-800 font-bold">${{ book.price }}</p>

      <div class="flex justify-between mt-4">
        <router-link v-if="!isAdminView" :to="`/books/edit/${book.id}`" class="text-blue-500 hover:underline">View Details</router-link>
        
        <div v-if="isAdminView" class="flex space-x-2">
          <button @click="$emit('edit', book.id)" class="bg-yellow-500 text-white px-4 py-2 rounded hover:bg-yellow-600">Edit</button>
          <button @click="$emit('delete', book.id)" class="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600">Delete</button>
        </div>

        <button v-if="!isAdminView" @click="addToCart(book)" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">Add to Cart</button>
      </div>
    </div>
  </div>
</template>


<script setup>
import { defineProps } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import { useShoppingCartStore } from '@/stores/shoppingCartStore';
import { useRouter } from 'vue-router';
import fallbackImage from '@/assets/books.jpg';

const props = defineProps({
  book: {
    type: Object,
    required: true,
  },
  isAdminView: {
    type: Boolean,
    default: false,
  },
});

const shoppingCartStore = useShoppingCartStore();
const authStore = useAuthStore();
const router = useRouter();

function addToCart(book) {
  if (authStore.user) {
    try {
      const userId = localStorage.getItem('userId');
      if (userId) {
        shoppingCartStore.addItemToCart(userId, book)
          .then(() => {
            console.log("Item added to remote cart");
          })
          .catch(error => {
            console.error("Error adding to cart:", error);
          });
      }
    } catch (error) {
      console.error("Error adding to cart:", error);
    }
  } else {
    router.push('/login');
  }
}
</script>
