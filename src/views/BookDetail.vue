<template>
  <div v-if="book" class="book-detail p-4 mx-auto max-w-xl">
    <img :src="book.coverImage || 'https://picsum.photos/350/300?image=1073'" alt="Book Cover" class="w-full h-64 object-cover rounded-lg">
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
</template>

<script>
import { useBookStore } from '@/stores/bookStore';
import { useCartStore } from '@/stores/cartStore';
import { onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';

export default {
  setup() {
    const bookStore = useBookStore();
    const cartStore = useCartStore();
    const route = useRoute();

    const book = computed(() => bookStore.book);

    onMounted(async () => {
      await bookStore.fetchBook(route.params.id);
    });

    const addToCart = (book) => {
      cartStore.addItemToCart(book);
    };

    return {
      book,
      addToCart,
    };
  },
};
</script>

<style scoped>
.book-detail {
  @apply p-4 mx-auto max-w-xl;
}
</style>
