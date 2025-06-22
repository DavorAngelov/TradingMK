import React, {useRef, useEffect, useState} from 'react';
import * as THREE from 'three';
import {Play} from 'lucide-react';
import {Link, useNavigate} from 'react-router-dom';

const LandingPage = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen">
            {/* Navigation */}
            <nav className="flex items-center justify-between px-8 py-6">
                <div className="flex items-center space-x-2">
                    <span className="text-2xl font-bold">Trading<span className="text-blue-400">MK</span></span>
                </div>
                <button
                    className="border border-green-400 text-green-400 px-6 py-2 rounded-full hover:bg-green-400 hover:text-black transition-all duration-300 cursor-pointer"
                    onClick={() => navigate('/login')}
                >
                    Sign In / Sign Up
                </button>
            </nav>

            {/* Main Content */}

            <div className="flex flex-col items-center justify-center text-center px-8 mt-10 gap-20">
                <h1 className="text-5xl lg:text-6xl font-bold leading-tight" >
                    Create your
                    <br/>
                    <span className="text-transparent bg-clip-text bg-gradient-to-r from-green-400 to-blue-500">
              own Profile &
            </span>
                    <br/>
                    Start Trading
                </h1>

                <p className="text-xl text-gray-400 max-w-md ">
                    Investing and Trading made simple, affordable and accessible for you
                </p>

                <div className="flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-6">
                    <button
                        className="bg-gradient-to-r from-green-400 to-green-500 text-black px-8 py-4 rounded-full font-semibold hover:from-green-500 hover:to-green-600 transition-all duration-300 transform hover:scale-105"
                        onClick={() => navigate('/signup')}
                    >
                        Get Started!
                    </button>

                    <button
                        className="flex items-center space-x-3  hover:text-gray-300 transition-colors"  >
                        <Play className="w-6 h-6"/>
                        <Link className="font-medium" to="/dashboard" >How it Works</Link>
                    </button>
                </div>
            </div>
        </div>
    );
};

export default LandingPage;