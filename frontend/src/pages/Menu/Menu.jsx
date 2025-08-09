import React, { useState } from 'react';
import {NavLink, useNavigate} from 'react-router-dom';
import profilePic from '../../assets/images/davor-picture.jpg';
import defaultPic from '../../assets/images/default-profile.png'
import { ChevronDown, LogOut, Settings } from 'lucide-react';
import { jwtDecode } from 'jwt-decode';

const Menu = () => {
    const [open, setOpen] = useState(false);
    const navigate = useNavigate();
    const token = localStorage.getItem('accessToken');
    let username = '';

    if (token) {
        const decoded = jwtDecode(token);
        username = decoded.sub; //username
    }

    console.log(username);

    const toggleDropdown = () => setOpen(!open);

    return (
        <nav className="w-full fixed top-0 left-0 bg-white/20 text-black backdrop-blur-md shadow-lg rounded-xl z-50 border border-blue-100">
            <div className="max-w-7xl mx-auto px-4 py-3 flex justify-between items-center">
                <div className="flex items-center space-x-6">
                    <div className="text-xl font-bold">Trading<span className="text-blue-400">MK</span></div>
                    <NavLink
                        to="/dashboard"
                        className={({ isActive }) =>
                            isActive ? 'border-b-2 border-blue-400 pb-1 hover:text-gray-600' : 'hover:text-gray-600'
                        }
                    >
                        Dashboard
                    </NavLink>
                    <NavLink
                        to="/portfolio"
                        className={({ isActive }) =>
                            isActive ? 'border-b-2 border-blue-400 pb-1 hover:text-gray-600' : 'hover:text-gray-600'
                        }
                    >
                        My Portfolio
                    </NavLink>
                </div>

                {/* Right side: User info */}
                <div className="relative flex items-center gap-2">
                    <NavLink to="/analysis" className="mr-20 hover:text-gray-600">Analysis</NavLink>
                    <img
                        src={defaultPic}
                        alt="Profile"
                        className="w-10 h-10 rounded-full object-cover"
                    />
                    <span className="font-medium">{username}</span>
                    <button
                        className="flex items-center space-x-2 hover:text-gray-600 focus:outline-none"
                        onClick={toggleDropdown}
                    >
                        <ChevronDown
                            className={`w-4 h-4 transform transition-transform duration-300 ${
                                open ? 'rotate-180' : 'rotate-0'
                            }`}
                        />
                    </button>

                    {open && (
                        <div className="absolute right-0 left-36 mt-40 w-48 bg-white border-blue-50 rounded-xl shadow-md z-50 ">
                            <NavLink
                                to="/settings"
                                className="block px-4 py-2 hover:bg-gray-100 text-sm rounded-xl flex gap-2"
                            >
                                <Settings />
                                Settings
                            </NavLink>
                            <button
                                className="w-full cursor-pointer text-left px-4 py-2 hover:bg-gray-100 text-sm rounded-xl text-red-500 flex gap-2"
                                onClick={() => { localStorage.removeItem('username'); navigate("/")}}
                            >
                                <LogOut />
                                Logout
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Menu;