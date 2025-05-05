import {Link} from "react-router-dom";

export function Header() {
    return (
        <header className="header">
            <div className="header__container">
                <div className="header__logo">
                    <Link to="/" className="header__logo-link">
                        <img
                            src="../../public/images/logo.png"
                            alt="Logo"
                            className="header__logo-image"
                        />
                    </Link>
                </div>

                <nav className="header__nav">
                    <ul className="header__nav-list">
                        <li className="header__nav-item">
                            <Link to="/" className="header__nav-link">
                                Home
                            </Link>
                        </li>
                        <li className="header__nav-item">
                            <Link to="/tracks" className="header__nav-link">
                                All Tracks
                            </Link>
                        </li>
                        <li className="header__nav-item">
                            <Link to="/about" className="header__nav-link">
                                About
                            </Link>
                        </li>
                    </ul>
                </nav>

                <div className="header__auth">
                    <Link to="/login" className="header__auth-link">
                        Sign In
                    </Link>
                    <Link to="/register" className="header__auth-link">
                        Sign Up
                    </Link>
                </div>
            </div>
        </header>
    )
}