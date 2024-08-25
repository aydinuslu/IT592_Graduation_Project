<template>
  <div>
    <!-- Toast Notification -->
    <Toast v-if="toastMessage" :message="toastMessage" @close="toastMessage = ''"/>

    <div v-if="book" class="book-detail p-4 mx-auto max-w-xl">
      <img :src="book.coverImage || fallbackImage" alt="Book Cover" class="w-full h-64 object-cover rounded-lg">
      <div class="mt-4">
        <h2 class="text-2xl font-bold">{{ book.title }}</h2>
        <p class="text-gray-600">Author: {{ book.author }}</p>
        <p class="text-gray-800 font-semibold">Price: ${{ book.price }}</p>
        <p class="text-gray-600">Stock: {{ book.stock }}</p>
        <button @click="addToCart(book)" class="bg-blue-500 text-white px-4 py-2 mt-4 rounded hover:bg-blue-600">Add to Cart</button>
      </div>
    </div>
    <div v-else class="p-4 text-center text-gray-600">
      Loading book details...
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useBookStore } from '@/stores/bookStore';
import { useShoppingCartStore } from '@/stores/shoppingCartStore';
import { useAuthStore } from '@/stores/authStore';
import Toast from '@/components/Toast.vue';  // Import the Toast component
import fallbackImage from '@/assets/books.jpg';  // Import the fallback image

const route = useRoute();
const router = useRouter();
const bookStore = useBookStore();
const shoppingCartStore = useShoppingCartStore();
const authStore = useAuthStore();

const book = computed(() => bookStore.book);
const toastMessage = ref('');  // State to hold the toast message

onMounted(async () => {
  await bookStore.fetchBook(route.params.id);
});

const addToCart = async (book) => {
  if (!authStore.user) {
    router.push('/login');  // Redirect to login if user is not authenticated
  } else {
    try {
      const userId = localStorage.getItem('userId');
      if (userId) {
        // Check if book is in the cart
        const cartItem = shoppingCartStore.cart.items.find(item => item.bookId === book.id);
        if (cartItem) {
          // Update quantity if already in cart
          const newQuantity = cartItem.quantity + 1;
          if (newQuantity <= book.stock) {
            await shoppingCartStore.updateItemQuantity(userId, book.id, newQuantity);
            toastMessage.value = `${book.title} quantity updated in cart!`;
          } else {
            alert("Not enough stock available. Only items in stock can be added to cart.");
          }
        } else {
          // Add new item to cart if not in cart
          if (book.stock > 0) {
            await shoppingCartStore.addItemToCart(userId, book);
            toastMessage.value = `${book.title} added to cart!`;
          } else {
            alert("This book is out of stock.");
          }
        }
      }
    } catch (error) {
      console.error("Error adding to cart:", error);
    }
  }
};
</script>
