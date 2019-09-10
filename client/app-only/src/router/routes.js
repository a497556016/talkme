export default [
    {path: '/', redirect: '/chat'},
    {path: '/chat', component: () => import('@/views/Chat')},
    {path: '/search_user', component: () => import('@/views/SearchUser')},
    {path: '/login', component: () => import("@/views/Login")},
    {path: '/register', component: () => import("@/views/Register")},
    {path: '/user_center', component: () => import("@/views/UserCenter")},
    {path: '/user_edit', component: () => import("@/views/UserEdit")}
]