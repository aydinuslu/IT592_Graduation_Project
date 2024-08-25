<template>
    <div class="container mx-auto py-8">
      <h1 class="text-center text-3xl font-bold my-8">Manage Books</h1>
      <p v-if="books.length === 0" class="text-center text-gray-500">No books available to manage.</p>
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        <div v-for="book in books" :key="book.id" class="bg-white shadow-lg rounded-lg overflow-hidden">
          <img :src="book.coverImage || fallbackImage" alt="Book Cover" class="w-full h-48 object-cover">
          <div class="p-4">
            <h2 class="text-xl font-semibold">{{ book.title }}</h2>
            <p class="text-gray-600">{{ book.author }}</p>
            <p class="text-gray-800 font-bold">${{ book.price }}</p>
            <div class="flex justify-between mt-4 space-x-2">
              <button @click="editBook(book.id)" class="bg-yellow-500 text-white px-4 py-2 rounded hover:bg-yellow-600">Edit</button>
              <button @click="deleteBook(book.id)" class="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600">Delete</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useBookStore } from '@/stores/bookStore';
  import { useRouter } from 'vue-router';
  import fallbackImage from '@/assets/books.jpg';
  
  const bookStore = useBookStore();
  const router = useRouter();
  const books = ref([]);
  
  onMounted(async () => {
    await bookStore.fetchBooks();
    books.value = bookStore.books;
  });
  
  function editBook(bookId) {
    router.push(`/edit-book/${bookId}`);
  }
  
  async function deleteBook(bookId) {
    if (confirm("Are you sure you want to delete this book?")) {
      try {
        await bookStore.deleteBook(bookId);
        books.value = books.value.filter(book => book.id !== bookId); // Remove the deleted book from the list
      } catch (error) {
        console.error("Error deleting book:", error);
      }
    }
  }
  </script>
  