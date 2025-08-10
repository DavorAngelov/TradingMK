import React, { useState } from "react";
import { ChevronLeft, ChevronRight, BookOpen, LineChart } from "lucide-react";
import Menu from "../Menu/Menu.jsx";

const videoData = [
    {
        title: "Како да тргувате на Македонската берза ",
        youtubeId: "hJESIsF31Ws",
    },
    {
        title: "Конференција 2024 - Панел 3: Компаниите и берзата – флерт или вистинска врска?",
        youtubeId: "QZfiamwPQ9w?si=Bm0JTzN4983wpB7_",
    },

];

const Education = () => {
    const [currentIndex, setCurrentIndex] = useState(0);

    const prevVideo = () => {
        setCurrentIndex((prev) => (prev === 0 ? videoData.length - 1 : prev - 1));
    };

    const nextVideo = () => {
        setCurrentIndex((prev) => (prev === videoData.length - 1 ? 0 : prev + 1));
    };

    return (
        <div className="max-w-7xl mx-auto space-y-8 pt-20">

            <Menu/>
            <h3 className="text-4xl  text-gray-300 font-bold mb-8">Education</h3>
            <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm p-6">
                <h2 className="text-2xl font-semibold text-gray-800 flex items-center gap-2">
                    <BookOpen className="w-6 h-6 text-green-500" />
                    Analysis & Education
                </h2>
                <p className="text-sm text-gray-600 mt-1">
                    Learn about investing, the Macedonian stock market, and improve your market analysis skills.
                </p>
            </div>

            {/* videos */}
            <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm p-6">
                <h3 className="text-xl font-semibold text-gray-800 mb-4 flex items-center gap-2">
                    <LineChart className="w-5 h-5 text-green-500" />
                    Educational Videos
                </h3>

                <div className="relative">

                    <div className="aspect-video bg-gray-100 rounded-lg overflow-hidden shadow-md">
                        <iframe
                            src={`https://www.youtube.com/embed/${videoData[currentIndex].youtubeId}`}
                            title={videoData[currentIndex].title}
                            className="w-full h-full"
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                            allowFullScreen
                        ></iframe>
                    </div>


                    <button
                        onClick={prevVideo}
                        className="absolute top-1/2 -translate-y-1/2 left-0 bg-white/70 p-2 rounded-full shadow hover:bg-white"
                    >
                        <ChevronLeft className="w-5 h-5 text-gray-800" />
                    </button>
                    <button
                        onClick={nextVideo}
                        className="absolute top-1/2 -translate-y-1/2 right-0 bg-white/70 p-2 rounded-full shadow hover:bg-white"
                    >
                        <ChevronRight className="w-5 h-5 text-gray-800" />
                    </button>
                </div>

                <div className="mt-4 text-center">
                    <h4 className="font-semibold text-gray-900">{videoData[currentIndex].title}</h4>
                    <p className="text-sm text-gray-500">{videoData[currentIndex].category}</p>
                </div>
            </div>

            {/* links */}
            <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm p-6 mb-4">
                <h3 className="text-xl font-semibold text-gray-800 mb-4">Extra Learning Resources</h3>
                <ul className="list-disc list-inside space-y-2 text-sm text-gray-600">
                    <li>
                        <a
                            href="https://www.mse.mk/en"
                            target="_blank"
                            className="text-green-600 hover:underline"
                        >
                            Macedonian Stock Exchange Official Website
                        </a>
                    </li>
                    <li>
                        <a
                            href="https://www.investopedia.com/"
                            target="_blank"
                            className="text-green-600 hover:underline"
                        >
                            Investopedia (Beginner to Advanced Investing Guides)
                        </a>
                    </li>

                </ul>
            </div>
        </div>
    );
};

export default Education;
