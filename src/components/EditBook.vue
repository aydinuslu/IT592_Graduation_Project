<template>
  <div class="container mx-auto p-4">
    <h2 class="text-2xl font-bold mb-4">Edit Book</h2>
    <form @submit.prevent="editBook" class="space-y-4">
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
        Save Changes
      </button>
    </form>

    <!-- Success/Error Message -->
    <div v-if="message" class="mt-4 p-4" :class="{'bg-green-100 text-green-700': success, 'bg-red-100 text-red-700': !success}">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useBookStore } from '@/stores/bookStore';

const route = useRoute();
const router = useRouter();
const bookStore = useBookStore();
const book = ref({ title: '', author: '', price: '', stock: '' });
const message = ref('');
const success = ref(false);

onMounted(async () => {
  const bookId = route.params.id;
  try {
    const response = await fetch(`${import.meta.env.VITE_BOOK_CATALOG_SERVICE_API}/edit/${bookId}`);
    if (!response.ok) throw new Error('Failed to load book data');
    book.value = await response.json();
  } catch (error) {
    console.error('Error fetching book:', error);
    message.value = 'Error loading book data';
  }
});

const editBook = async () => {
  try {
    const response = await fetch(`${import.meta.env.VITE_BOOK_CATALOG_SERVICE_API}/edit/${book.value.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(book.value),
    });
    if (response.ok) {
      success.value = true;
      message.value = 'Book updated successfully!';
      router.push('/manage-books');
    } else {
      success.value = false;
      message.value = `Failed to update book: ${response.statusText}`;
    }
  } catch (error) {
    success.value = false;
    message.value = `Error updating book: ${error.message}`;
  }
};
</script>

<style scoped>
.container {
  max-width: 600px;
}
</style>
