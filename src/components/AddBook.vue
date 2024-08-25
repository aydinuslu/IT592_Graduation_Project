<template>
  <div class="container mx-auto p-4">
    <h2 class="text-2xl font-bold mb-4">Add Book</h2>
    <form @submit.prevent="addBook" class="space-y-4">
      <div>
        <label for="title" class="block text-gray-700">Title</label>
        <input
          v-model="book.title"
          type="text"
          id="title"
          class="w-full p-2 border border-gray-300 rounded"
          required
        />
      </div>
      <div>
        <label for="author" class="block text-gray-700">Author</label>
        <input
          v-model="book.author"
          type="text"
          id="author"
          class="w-full p-2 border border-gray-300 rounded"
          required
        />
      </div>
      <div>
        <label for="price" class="block text-gray-700">Price</label>
        <input
          v-model="book.price"
          type="number"
          step="0.01"
          id="price"
          class="w-full p-2 border border-gray-300 rounded"
          required
        />
      </div>
      <div>
        <label for="stock" class="block text-gray-700">Stock</label>
        <input
          v-model="book.stock"
          type="number"
          id="stock"
          class="w-full p-2 border border-gray-300 rounded"
          required
        />
      </div>
      <button
        type="submit"
        class="bg-teal-500 text-white py-2 px-4 rounded hover:bg-teal-600"
      >
        Add Book
      </button>
    </form>

    <!-- Success/Error Message -->
    <div v-if="message" class="mt-4 p-4" :class="{'bg-green-100 text-green-700': success, 'bg-red-100 text-red-700': !success}">
      {{ message }}
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  setup() {
    const book = ref({
      title: '',
      author: '',
      price: '',
      stock: '',
    });

    const message = ref('');
    const success = ref(false);

    const addBook = async () => {
      try {
        const response = await fetch('http://localhost:8082/api/books/add', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(book.value),
        });

        if (response.ok) {
          success.value = true;
          message.value = 'Book added successfully!';
          // Clear the form fields
          book.value = { title: '', author: '', price: '', stock: '' };
        } else {
          success.value = false;
          const errorData = await response.json();
          message.value = `Failed to add book: ${errorData.message || response.statusText}`;
        }
      } catch (error) {
        success.value = false;
        message.value = `Error adding book: ${error.message}`;
      }
    };

    return {
      book,
      addBook,
      message,
      success,
    };
  },
};
</script>

<style scoped>
.container {
  max-width: 600px;
}
</style>
