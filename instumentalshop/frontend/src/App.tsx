import {BrowserRouter, Route, Routes} from 'react-router-dom'
import {AuthProvider} from './context/AuthContext'
import {MainLayout} from './layouts/MainLayout'
import {RequireAuth} from './components/RequireAuth'

import {Home} from './pages/Home'
import {Login} from './pages/Login'
import {Register} from './pages/Register'
import {Tracks} from './pages/Tracks'
// import {Profile} from './pages/Profile'
// import {Purchases} from './pages/Purchases'
// import {Upload} from './pages/Upload'
// import {Sales} from './pages/Sales'
// import {Chats} from './pages/Chats'
// import {AdminPurchases} from './pages/AdminPurchases'

export default function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <Routes>
                    <Route path="/" element={<MainLayout/>}>
                        {/* available for all */}
                        <Route index element={<Home/>}/>
                        <Route path="login" element={<Login/>}/>
                        <Route path="register" element={<Register/>}/>
                        <Route path="tracks" element={<Tracks/>}/>

                        {/* available only for logged in user and producer */}
                        <Route element={<RequireAuth allowedRoles={['user', 'producer']}/>}>
                            {/*<Route path="profile" element={<Profile/>}/>*/}
                            {/*<Route path="purchases" element={<Purchases/>}/>*/}
                            {/*<Route path="chats" element={<Chats/>}/>*/}
                        </Route>

                        {/* available onlyfor producer */}
                        <Route element={<RequireAuth allowedRoles={['producer']}/>}>
                            {/*<Route path="upload" element={<Upload/>}/>*/}
                            {/*<Route path="sales" element={<Sales/>}/>*/}
                        </Route>

                        {/* available only for admin */}
                        <Route element={<RequireAuth allowedRoles={['admin']}/>}>
                            {/*<Route path="admin/purchases" element={<AdminPurchases/>}/>*/}
                        </Route>

                    </Route>
                </Routes>
            </AuthProvider>
        </BrowserRouter>
    )
}