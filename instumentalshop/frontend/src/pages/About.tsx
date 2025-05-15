import { FC } from 'react';
import { Link } from 'react-router-dom';

import javaLogo      from '/images/java_logo.png';
import springLogo    from '/images/spring_logo.png';
import tsLogo        from '/images/ts_logo.png';
import reactLogo     from '/images/react_logo.png';
import tailwindLogo  from '/images/tailwind_logo.png';
import postgresLogo  from '/images/postgre_logo.png';

const tech = [
    { img: javaLogo,     alt: 'Java' },
    { img: springLogo,   alt: 'Spring Boot' },
    { img: tsLogo,       alt: 'TypeScript' },
    { img: reactLogo,    alt: 'React' },
    { img: tailwindLogo, alt: 'Tailwind CSS' },
    { img: postgresLogo, alt: 'PostgreSQL' },
];

const About: FC = () => (
    <main className="min-h-screen bg-[#111] text-white">
        <div className="max-w-6xl mx-auto px-5">

            {/* ───── Hero ───── */}
            <section className="flex flex-col items-center text-center pt-24 pb-32 space-y-10">
                <h1 className="text-4xl sm:text-5xl md:text-6xl font-extrabold tracking-wide">
                    JOIN&nbsp;OUR&nbsp;MUSIC&nbsp;PLATFORM
                </h1>

                <p className="max-w-3xl text-gray-300 text-lg">
                    We connect producers and artists so they can collaborate
                    and unlock their full potential.
                </p>

                {/* кнопки с увеличенным отступом */}
                <div className="flex gap-4 mt-12">   {/* ← mt-12 вместо прежнего */}
                    <a
                        href="#learn-more"
                        className="px-7 py-3 rounded-full bg-emerald-500 hover:bg-emerald-400 text-gray-900 font-semibold transition"
                    >
                        Learn More
                    </a>
                    <Link
                        to="/login"
                        className="px-7 py-3 rounded-full bg-gray-600 hover:bg-gray-500 font-semibold transition"
                    >
                        Work at BeatStars
                    </Link>
                </div>
            </section>

            {/* ───── Logos ───── */}
            <section className="pb-28">
                <h2 className="text-center text-2xl font-semibold mb-16 tracking-wide">
                    WEBSITE WAS DEVELOPED USING:
                </h2>

                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-y-14 place-items-center">
                    {tech.map(t => (
                        <div key={t.alt} className="flex flex-col items-center">
                            <img src={t.img} alt={t.alt} className="h-20 w-auto object-contain" />
                            <span className="mt-4 text-sm text-gray-300">{t.alt}</span>
                        </div>
                    ))}
                </div>
            </section>

            {/* ───── Cards ───── */}
            <section id="learn-more" className="grid gap-10 md:grid-cols-3 pb-32">
                <article className="about-card">
                    <h3 className="about-card__title">Project Description</h3>
                    <p className="about-card__text">
                        Full-stack marketplace where producers sell beats and
                        artists license them. Includes streaming preview,
                        licence checkout, PDF generation and chat.
                    </p>
                </article>

                <article className="about-card">
                    <h3 className="about-card__title">Technologies Used</h3>
                    <ul className="list-disc ml-5 space-y-1 text-gray-300 text-sm">
                        <li>Java 17 + Spring Boot 3 for REST backend</li>
                        <li>JWT security &amp; WebSocket chat</li>
                        <li>PostgreSQL 15 for data storage</li> {/* обновлено */}
                        <li>React 18 + TypeScript frontend</li>
                        <li>Tailwind CSS for styling</li>
                    </ul>
                </article>

                <article className="about-card">
                    <h3 className="about-card__title">Goals and Motivation</h3>
                    <p className="about-card__text">
                        Semester project to master modern Java backend,
                        strongly-typed React, and utility-first CSS.
                    </p>
                </article>
            </section>
        </div>
    </main>
);

export default About;
