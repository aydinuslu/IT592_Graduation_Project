<template>
  <div>
    <h2>Edit Book</h2>
    <form @submit.prevent="editBook">
      <label for="title">Title</label>
      <input v-model="book.title" type="text" id="title" />
      <label for="author">Author</label>
      <input v-model="book.author" type="text" id="author" />
      <label for="price">Price</label>
      <input v-model="book.price" type="number" id="price" />
      <button type="submit">Edit Book</button>
    </form>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';

export default {
  setup() {
    const route = useRoute();
    const book = ref({
      title: '',
      author: '',
      price: '',
    });

    onMounted(async () => {
      const bookId = route.params.id;
      try {
        const response = await fetch(`${import.meta.env.VITE_BOOK_CATALOG_SERVICE_API}/${bookId}`);
        book.value = await response.json();
      } catch (error) {
        console.error('Error fetching book:', error);
      }
    });

    const editBook = async () => {
      try {
        const response = await fetch(`${import.meta.env.VITE_BOOK_CATALOG_SERVICE_API}/${book.value.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(book.value),
        });
        if (response.ok) {
          // handle success
        }
      } catch (error) {
        console.error('Error editing book:', error);
      }
    };

    return {
      book,
      editBook,
    };
  },
};
</script>
