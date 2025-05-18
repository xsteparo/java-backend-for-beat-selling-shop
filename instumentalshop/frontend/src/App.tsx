// src/App.tsx (или где вы их описываете)
import {BrowserRouter, Route, Routes} from 'react-router-dom'
import {MainLayout} from './layouts/MainLayout'
import {Home} from './pages/Home'
import {Login} from './pages/Login'
import {Register} from './pages/Register'
import {Tracks} from './pages/Tracks'
// import { Profile }     from './pages/Profile'
import Purchases from './pages/Purchases'
import Chats from './pages/Chats'
import {Upload} from './pages/Upload'
// import { Sales }       from './pages/Sales'
// import { AdminPurchases } from './pages/AdminPurchases'
import {RequireAuth} from './components/RequireAuth'
import {RequireGuest} from './components/RequireGuest'
import {CartProvider, useCart} from "./context/CartContext.tsx";

import {Cart} from "./components/Cart.tsx";
import {AuthProvider} from './context/AuthContext.tsx'
import About from "./pages/About.tsx";

export const App = () => (
    <BrowserRouter>
        <AuthProvider>
            <CartProvider>
                {/* глобальная панель-корзина */}
                <CartHost/>
                <Routes>
                    <Route path="/" element={<MainLayout/>}>
                        {/* публичная главная */}
                        <Route index element={<Home/>}/>
                        <Route path="/about" element={<About/>}/>

                        {/* только для гостей */}
                        <Route element={<RequireGuest/>}>
                            <Route path="login" element={<Login/>}/>
                            <Route path="register" element={<Register/>}/>
                        </Route>

                        {/* доступно всем (в том числе гостям) */}
                        <Route path="tracks" element={<Tracks/>}/>

                        {/* только для залогиненных user или producer */}
                        <Route element={<RequireAuth allowedRoles={['customer', 'producer']}/>}>
                            {/*<Route path="profile"   element={<Profile />}   />*/}
                            <Route path="purchases" element={<Purchases/>}/>
                            <Route path="chats"     element={<Chats />}     />
                            <Route path="/chats/:roomId" element={<Chats />} />
                        </Route>

                        {/* только для producer */}
                        <Route element={<RequireAuth allowedRoles={['producer']}/>}>
                            <Route path="upload" element={<Upload/>}/>
                            {/*<Route path="sales"  element={<Sales />}  />*/}
                        </Route>

                        {/* только для admin */}
                        <Route element={<RequireAuth allowedRoles={['admin']}/>}>
                            {/*<Route path="admin/purchases" element={<AdminPurchases />} />*/}
                        </Route>
                    </Route>
                </Routes>
            </CartProvider>

        </AuthProvider>
    </BrowserRouter>
)


function CartHost() {
    const {items, open, removeIdx, close} = useCart();
    return <Cart items={items} open={open} onRemove={removeIdx} onClose={close}/>;
}

export default App;
