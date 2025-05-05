import {Outlet} from 'react-router-dom'
import {Header} from '../components/Header'
import {Footer} from '../components/Footer'

export function MainLayout() {
    return (
        <>
            <Header/>
            <main className="min-h-screen bg-gradient-to-b from-black to-green-900 text-white p-4">
                <Outlet/>
            </main>
            <Footer/>
        </>
    )
}