<template>
    <div>
      <h2 class="text-2xl font-bold mb-4">{{ isEditMode ? 'Edit Book' : 'Add New Book' }}</h2>
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div>
          <label for="title" class="block text-sm font-medium text-gray-700">Title:</label>
          <input
            v-model="title"
            id="title"
            type="text"
            required
            class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
          />
        </div>
        <div>
          <label for="author" class="block text-sm font-medium text-gray-700">Author:</label>
          <input
            v-model="author"
            id="author"
            type="text"
            required
            class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
          />
        </div>
        <div>
          <label for="price" class="block text-sm font-medium text-gray-700">Price:</label>
          <input
            v-model="price"
            id="price"
            type="number"
            step="0.01"
            required
            class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
          />
        </div>
        <div>
          <label for="stock" class="block text-sm font-medium text-gray-700">Stock:</label>
          <input
            v-model="stock"
            id="stock"
            type="number"
            required
            class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
          />
        </div>
        <button type="submit" class="w-full bg-blue-500 text-white py-2 rounded-md hover:bg-blue-600">
          {{ isEditMode ? 'Update Book' : 'Add Book' }}
        </button>
      </form>
      <div v-if="error" class="mt-4 p-4 bg-red-100 text-red-700 rounded">
        Error: {{ error }}
      </div>
    </div>
  </template>
  
  <script>
  import { useRoute } from 'vue-router';
  import { useBookStore } from '@/stores/bookStore';
  
  export default {
    name: 'BookForm',
    props: {
      book: {
        type: Object,
        default: null,
      },
    },
    data() {
      return {
        title: this.book ? this.book.title : '',
        author: this.book ? this.book.author : '',
        price: this.book ? this.book.price : 0,
        stock: this.book ? this.book.stock : 0,
      };
    },
    computed: {
      isEditMode() {
        return !!this.book;
      },
      error() {
        const store = useBookStore();
        return store.error;
      },
    },
    async created() {
      if (this.isEditMode) {
        const store = useBookStore();
        await store.fetchBookById(this.book.id);
        const book = store.book;
        this.title = book.title;
        this.author = book.author;
        this.price = book.price;
        this.stock = book.stock;
      }
    },
    methods: {
      async handleSubmit() {
        const store = useBookStore();
        const bookData = {
          title: this.title,
          author: this.author,
          price: parseFloat(this.price),
          stock: parseInt(this.stock, 10),
        };
  
        if (this.isEditMode) {
          await store.updateBook(this.book.id, bookData);
        } else {
          await store.addBook(bookData);
        }
  
        this.$router.push('/books');
      },
    },
  };
  </script>
  
  <style scoped>
  /* Add styles for the BookForm component if needed */
  </style>
  