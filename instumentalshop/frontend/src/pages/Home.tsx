import React, { useState, FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'

export const Home: React.FC = () => {
    const [searchTerm, setSearchTerm] = useState('')
    const navigate = useNavigate()

    const handleSearch = (e: FormEvent) => {
        e.preventDefault()
        // переходим на страницу треков с query-параметром ?search=...
        navigate(`/tracks?search=${encodeURIComponent(searchTerm)}`)
    }

    return (
        <main>
            <div className="container">

                {/* Секция поиска */}
                <section className="home-section">
                    <div className="search-content">
                        <h1 className="search-title">YOUR FIRST HIT STARTS HERE</h1>
                        <div className="search-box">
                            <form
                                id="form-search-beats"
                                className="form-search-beats"
                                onSubmit={handleSearch}
                            >
                                <input
                                    type="text"
                                    id="input-search-beats"
                                    name="search"
                                    placeholder="Type beat name..."
                                    className="search-input"
                                    value={searchTerm}
                                    onChange={e => setSearchTerm(e.target.value)}
                                />
                                <button
                                    id="btn-search-beats-text"
                                    className="btn-search btn-search-text"
                                    type="submit"
                                >
                                    Search
                                </button>
                            </form>
                        </div>
                        <div className="trending">
                            <span>What's trending right now:</span>
                            <div className="tags">
                                <a
                                    href="/tracks?search=ken%20carson"
                                    className="tag"
                                >
                                    ken carson
                                </a>
                                <a
                                    href="/tracks?search=future%20type%20beat"
                                    className="tag"
                                >
                                    future type beat
                                </a>
                                <a
                                    href="/tracks?search=sewerstvl"
                                    className="tag"
                                >
                                    sewerstvl
                                </a>
                                <a
                                    href="/tracks?search=2hollis"
                                    className="tag"
                                >
                                    2hollis
                                </a>
                            </div>
                        </div>
                    </div>
                </section>

                {/* Первая контент-секция */}
                <section className="home-section">
                    <div className="section-content">
                        <div className="text-content">
                            <h2 className="section-title">Kickstart your music career today</h2>
                            <ul>
                                <li>
                                    <strong>The largest marketplace for high quality beats</strong>
                                    <br />
                                    Access over 8 million beats from our growing community of producers around the world.
                                </li>
                                <li>
                                    <strong>Seamless purchasing experience</strong>
                                    <br />
                                    We keep it effortless. Browse your favorite genres and purchase with ease – all within one platform.
                                </li>
                                <li>
                                    <strong>Simple licensing options</strong>
                                    <br />
                                    Contracts don’t have to be confusing. Spend less time scratching your head and more time recording your next hit.
                                </li>
                                <li>
                                    <strong>A community that understands you</strong>
                                    <br />
                                    We’re creators just like you. Whether you need our support team or want to collaborate with like-minded creatives, there’s a home for you.
                                </li>
                            </ul>
                            <Link to="/register" className="btn-primary">
                                Get started
                            </Link>
                        </div>
                        <div className="image-content">
                            <img
                                src="/images/home1.jpg"
                                alt="Пример трека"
                            />
                        </div>
                    </div>
                </section>

                {/* Вторая контент-секция */}
                <section className="home-section">
                    <div className="section-content">
                        <div className="image-content">
                            <img
                                src="/images/home2.jpg"
                                alt="Пример трека"
                            />
                        </div>
                        <div className="text-content">
                            <h2 className="section-title">Elevate Your Sound with Top Producers</h2>
                            <ul>
                                <li>
                                    <strong>Exclusive Collection of Beats</strong>
                                    <br />
                                    Discover unique beats crafted by top-tier producers to make your music stand out.
                                </li>
                                <li>
                                    <strong>Flexible Licensing Agreements</strong>
                                    <br />
                                    Secure the rights you need quickly with our straightforward licensing options.
                                </li>
                                <li>
                                    <strong>Connect with Industry Experts</strong>
                                    <br />
                                    Join a network of talented artists and producers to collaborate and grow your musical career.
                                </li>
                                <li>
                                    <strong>Advanced Search and Filters</strong>
                                    <br />
                                    Find the perfect beat for your project using our powerful, user-friendly search tools tailored to your needs.
                                </li>
                            </ul>
                            <Link to="/about" className="btn-primary">
                                Get started
                            </Link>
                        </div>
                    </div>
                </section>

            </div>
        </main>
    )
}