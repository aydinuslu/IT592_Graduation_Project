<template>
  <div class="container mx-auto px-4">
    <h1 class="text-center text-3xl font-bold my-8">Shopping Cart</h1>

    <!-- Error Message -->
    <div v-if="shoppingCartStore.error" class="bg-red-100 text-red-700 p-4 mb-4 rounded">
      {{ shoppingCartStore.error }}
    </div>

    <!-- Loading Spinner -->
    <div v-if="isLoading" class="flex justify-center items-center">
      <div class="text-xl text-gray-600">Loading your cart...</div>
    </div>

    <!-- Cart Items -->
    <div v-if="!isLoading && cartItems && cartItems.length > 0">
      <div
        v-for="item in cartItems"
        :key="item.bookId"
        class="flex items-center justify-between p-4 bg-white shadow-md rounded-lg mb-4"
      >
        <div class="flex items-center">
          <img
            :src="item.book?.coverImage || fallbackImage"
            alt="Book Cover"
            class="w-20 h-24 object-cover rounded-lg mr-4"
          />
          <div>
            <h2 class="text-xl font-semibold">{{ item.book?.title || 'Unknown Title' }}</h2>
            <p class="text-gray-600">Author: {{ item.book?.author || 'Unknown Author' }}</p>
            <p class="text-gray-800 font-bold">Price: ${{ item.book?.price || '0.00' }}</p>
          </div>
        </div>
        <div class="flex items-center">
          <button
            @click="updateQuantity(item.bookId, item.quantity - 1)"
            class="bg-gray-300 px-2 py-1 rounded hover:bg-gray-400"
            :disabled="!item.book || item.quantity <= 1"
          >
            -
          </button>
          <span class="mx-2">{{ item.quantity }}</span>
          <button
            @click="updateQuantity(item.bookId, item.quantity + 1)"
            class="bg-gray-300 px-2 py-1 rounded hover:bg-gray-400"
            :disabled="!item.book"
          >
            +
          </button>
          <button
            @click="removeFromCart(item.bookId)"
            class="bg-red-500 text-white px-4 py-2 ml-4 rounded hover:bg-red-600"
          >
            Remove
          </button>
        </div>
      </div>
      <div class="text-right text-2xl font-bold mt-4">
        Total: ${{ totalPrice.toFixed(2) }}
      </div>
    </div>

    <!-- Empty Cart Message -->
    <div v-else-if="!isLoading">
      <p class="text-center text-gray-600">Your cart is empty.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useShoppingCartStore } from '@/stores/shoppingCartStore';
import { useAuthStore } from '@/stores/authStore';
import fallbackImage from '@/assets/books.jpg';  // Import the fallback image

const shoppingCartStore = useShoppingCartStore();
const authStore = useAuthStore();

const isLoading = ref(true); // Loading state

onMounted(async () => {
  console.log("Component Mounted, isLoading:", isLoading.value);
  
  const userId = localStorage.getItem('userId');
  if (userId) {
    console.log("Shopping Cart mounted, fetching cart for user:", userId);
    await shoppingCartStore.fetchCart(userId);
    console.log("Cart Items after fetch:", shoppingCartStore.cart.items);
    await fetchBookDetails(shoppingCartStore.cart.items);
  } else {
    console.warn("User ID not found in localStorage.");
  }
  
  isLoading.value = false; // Set loading to false after fetching data
  console.log("Fetching complete, isLoading:", isLoading.value);
});

const cartItems = computed(() => shoppingCartStore.cart.items);
const totalPrice = computed(() =>
  cartItems.value.reduce((total, item) => {
    // Ensure the book object and its price are fully available
    if (item.book && item.book.price !== undefined) {
      return total + item.book.price * item.quantity;
    }
    return total; // If book data is incomplete, skip adding to the total
  }, 0)
);

// Function to fetch book details for each item in the cart
async function fetchBookDetails(cartItems) {
  console.log("Starting to fetch book details...");

  for (let item of cartItems) {
    try {
      console.log(`Fetching details for book ID: ${item.bookId}`);
      
      const response = await fetch(`http://74.248.84.174/api/books/edit/${item.bookId}`);
      
      console.log("Fetch response:", response);

      if (response.ok) {
        const book = await response.json();
        console.log("Fetched book details:", book);
        
        // Ensure the book data is assigned correctly
        if (book) {
          item.book = book;  // Assign fetched book details to item.book
          console.log("Assigned book details to item:", item.book);
        } else {
          console.error(`No book data returned for book ID: ${item.bookId}`);
        }
      } else {
        console.error(`Failed to fetch details for book ID: ${item.bookId}, Status: ${response.status}`);
      }
    } catch (error) {
      console.error(`Error fetching details for book ID: ${item.bookId}`, error);
    }
  }
  
  // Trigger reactivity by replacing the cart.items array
  shoppingCartStore.cart.items = [...cartItems]; 
  console.log("Completed fetching all book details:", shoppingCartStore.cart.items);
};


const updateQuantity = async (bookId, newQuantity) => {
  const userId = authStore.user?.id || localStorage.getItem('userId');
  
  if (userId && newQuantity > 0) {
    const item = cartItems.value.find(item => item.book?.id === bookId);
    const originalQuantity = item ? item.quantity : null; // Save original quantity

    if (item) {
      try {
        // Optimistically update the quantity in the local state
        item.quantity = newQuantity;
        cartItems.value = [...cartItems.value]; // Force reactivity update

        console.log(`Optimistically updated quantity for bookId ${bookId} to ${newQuantity}`);

        // Make the API call to update the quantity on the server
        const response = await fetch(`http://74.248.84.174/api/cart/${userId}/update/${bookId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ quantity: newQuantity }),
        });

        if (response.ok) {
          // If the update is successful, no further action is needed
          await shoppingCartStore.fetchCart(userId); // Optional: to ensure cart data is in sync
        } else {
          const errorText = await response.text();
          console.error(`Failed to update quantity for bookId ${bookId}: ${errorText}`);
          shoppingCartStore.error = errorText; // Set the error in the store

          // Revert to original quantity and total price
          revertQuantity(item, originalQuantity);
        }
      } catch (error) {
        console.error(`Error updating quantity for bookId ${bookId}:`, error);
        shoppingCartStore.error = error.message; // Set the error in the store

        // Revert to original quantity and total price
        revertQuantity(item, originalQuantity);
      }
    }
  }
};

// Helper function to revert quantity and re-calculate the total price
const revertQuantity = (item, originalQuantity) => {
  if (item && originalQuantity !== null) {
    item.quantity = originalQuantity;
    cartItems.value = [...cartItems.value]; // Force reactivity update
  }
};




const removeFromCart = async (bookId) => {
  const userId = authStore.user?.id || localStorage.getItem('userId');
  if (userId) {
    await shoppingCartStore.removeItemFromCart(userId, bookId);
  }
};
</script>
