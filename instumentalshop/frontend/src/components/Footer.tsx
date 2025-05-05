
export function Footer() {
    return (
        <footer className="footer">
            <div className="footer__container">

                <div className="footer__about">
                    <h3 className="footer__title">BeatShop</h3>
                    <p className="footer__description">
                        © 2025 BEATSHOP. All rights reserved.
                    </p>
                </div>

                <div className="footer__nav">
                    <h4 className="footer__nav-title">Useful Links</h4>
                    <ul className="footer__nav-list">
                        <li>
                            <a href="/terms" className="footer__nav-link">Terms of Use</a>
                        </li>
                    </ul>
                </div>

                <div className="footer__social">
                    <h4 className="footer__social-title">Social Media</h4>
                    <ul className="footer__social-list">
                        <li>
                            <a href="https://t.me/yourchannel" target="_blank" rel="noopener">
                                <img src="/images/telegram_logo.png" alt="Telegram" />
                            </a>
                        </li>
                        <li>
                            <a href="https://instagram.com/yourprofile" target="_blank" rel="noopener">
                                <img src="/images/instagram_logo.png" alt="Instagram" />
                            </a>
                        </li>
                        <li>
                            <a href="https://linkedin.com/in/yourprofile" target="_blank" rel="noopener">
                                <img src="/images/linkedin_logo.png" alt="LinkedIn" />
                            </a>
                        </li>
                    </ul>
                </div>

            </div>
        </footer>
    )
}