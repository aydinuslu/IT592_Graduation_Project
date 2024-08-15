<template>
  <div>
    <h2>Add Book</h2>
    <form @submit.prevent="addBook">
      <label for="title">Title</label>
      <input v-model="book.title" type="text" id="title" />
      <label for="author">Author</label>
      <input v-model="book.author" type="text" id="author" />
      <label for="price">Price</label>
      <input v-model="book.price" type="number" id="price" />
      <button type="submit">Add Book</button>
    </form>
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
    });

    const addBook = async () => {
      try {
        const response = await fetch(`${import.meta.env.VITE_BOOK_CATALOG_SERVICE_API}`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(book.value),
        });
        if (response.ok) {
          // handle success
        }
      } catch (error) {
        console.error('Error adding book:', error);
      }
    };

    return {
      book,
      addBook,
    };
  },
};
</script>
