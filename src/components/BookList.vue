<template>
  <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
    <ItemCard v-for="book in books" :key="book.id" :book="book" />
  </div>
</template>

<script>
import ItemCard from '@/components/ItemCard.vue'
import { useBookStore } from '@/stores/bookStore'

export default {
  name: 'BookList',
  components: { ItemCard },
  setup() {
    const bookStore = useBookStore()
    return { books: bookStore.books }
  }
}
</script>
