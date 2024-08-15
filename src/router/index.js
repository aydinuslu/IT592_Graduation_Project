import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/views/Home.vue';
import BookCatalog from '@/views/BookCatalog.vue';
import BookDetail from '@/views/BookDetail.vue';
import ShoppingCart from '@/views/ShoppingCart.vue';
import Checkout from '@/views/Checkout.vue';
import Login from '@/components/Login.vue';
import Register from '@/components/Register.vue';
import AddBook from '@/components/AddBook.vue';
import EditBook from '@/components/EditBook.vue';
import Order from '@/views/Order.vue';
import User from '@/views/User.vue';
import { useAuthStore } from '@/stores/authStore';
import { decodeJwt } from "jose";

const routes = [
  { path: '/', component: Home },
  { path: '/books', component: BookCatalog },
  { path: '/books/edit/:id', name: 'BookDetail', component: BookDetail },
  { path: '/cart', component: ShoppingCart },
  { path: '/checkout', component: Checkout, meta: { requiresAuth: true, roles: ['Customer'] }},
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/add-book', component: AddBook, meta: { requiresAuth: true, roles: ['Admin'] }},
  { path: '/edit-book/:id', component: EditBook, meta: { requiresAuth: true, roles: ['Admin'] }},
  { path: '/orders', component: Order, meta: { requiresAuth: true, roles: ['Customer'] }},
  { path: '/user', component: User, meta: { requiresAuth: true }},
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const token = authStore.token;
  const user = token ? decodeJwt(token) : null;

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!user) {
      next({ path: '/login' });
    } else if (to.matched.some(record => record.meta.roles && !record.meta.roles.includes(user.role))) {
      next({ path: '/' });
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;
