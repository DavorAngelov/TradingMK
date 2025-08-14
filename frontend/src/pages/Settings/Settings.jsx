import React from "react";
import {LogOut, User, Mail } from "lucide-react";
import Menu from "../Menu/Menu.jsx";
import {jwtDecode} from "jwt-decode";
import {useNavigate} from "react-router-dom";

const Settings = () => {
    const navigate = useNavigate();




    const token = localStorage.getItem('accessToken');
    let username = '';
    let email = '';

    if (token) {
        const decoded = jwtDecode(token);
        username = decoded.sub;
        email = decoded.email;//username

    }

    return (
        <div className=" max-w-7xl mx-auto space-y-8 pt-20 mb-4">
        <Menu/>
            <h3 className="text-4xl  text-gray-300 font-bold mb-8">Settings</h3>

            <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm p-6 space-y-6">
                <div className="flex items-center gap-3">
                    <User className="w-5 h-5 text-gray-500" />
                    <div>
                        <div className="text-sm text-gray-600">Username</div>
                        <div className="font-semibold text-gray-900">{username}</div>
                    </div>
                </div>


                <div className="flex items-center gap-3">
                    <Mail className="w-5 h-5 text-gray-500" />
                    <div>
                        <div className="text-sm text-gray-600">Email</div>
                        <div className="font-semibold text-gray-900">{email}</div>
                    </div>
                </div>

            </div>


            <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm p-6 flex justify-between items-center ">
                <div>
                    <div className="text-lg font-semibold text-gray-800">Logout</div>
                    <div className="text-sm text-gray-600">Sign out of your account</div>
                </div>
                <button
                    onClick={() => {localStorage.removeItem('accessToken'); console.log("removed"); navigate("/")}}
                    className="flex items-center gap-2 bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg cursor-pointer"
                >
                    <LogOut className="w-5 h-5"  />
                    Logout
                </button>
            </div>
        </div>
    );
};

export default Settings;
