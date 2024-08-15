<template>
  <div>
    <h1 class="text-center text-3xl font-bold my-8">Book Catalog</h1>
    <div class="container mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
      <div v-for="book in books" :key="book.id" class="bg-white shadow-lg rounded-lg overflow-hidden">
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
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useBookStore } from '@/stores/bookStore';
import { useCartStore } from '@/stores/cartStore';

export default {
  setup() {
    const bookStore = useBookStore();
    const cartStore = useCartStore();
    const books = ref([]);

    onMounted(async () => {
      await bookStore.fetchBooks();
      books.value = bookStore.books;
    });

    const addToCart = (book) => {
      cartStore.addItemToCart(book);
    };

    return {
      books,
      addToCart,
    };
  },
};
</script>
