import React, { useState } from 'react';
import {useNavigate} from "react-router-dom";

const SignupPage = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const signup = async () => {
        const response = await fetch('http://localhost:8080/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        const data = await response.json();
        const token = data.token;


        localStorage.setItem('accessToken', token);


        try {
            const { jwtDecode } = await import('jwt-decode');
            const decoded = jwtDecode(token);
            localStorage.setItem('username', decoded.sub);
        } catch (err) {
            console.error('failed to decode token:   ', err);
        }

        return "signup successfull";
    };
    const handleSignup = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            const message = await signup();
            setSuccess(message);
        } catch (err) {
            setError(err.message || 'Signup failed');
        }
    };

    return (
        <div>



            <nav className="flex items-center justify-between px-8 py-6">
                <div className="flex items-center space-x-2">
                    <span className="text-2xl font-bold">Trading<span className="text-blue-400">MK</span></span>
                </div>
                <button
                    className="border border-green-400 text-green-400 px-6 py-2 rounded-full hover:bg-green-400 hover:text-black transition-all duration-300 cursor-pointer"
                    onClick={() => navigate('/')}
                >
                    Back
                </button>
            </nav>
            <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
            <h2 className="text-2xl mb-4 font-semibold">Sign Up</h2>
            <form onSubmit={handleSignup} className="space-y-4">
                <input
                    type="text"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                    placeholder="Username"
                    className="w-full p-2 border rounded"
                    required
                />
                <input
                    type="email"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                    placeholder="Email"
                    className="w-full p-2 border rounded"
                    required
                />
                <input
                    type="password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    placeholder="Password"
                    className="w-full p-2 border rounded"
                    required
                />
                <button type="submit" className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700">
                    Sign Up
                </button>
            </form>
            {error && <p className="text-red-600 mt-4">{error}</p>}
            {success && <p className="text-green-600 mt-4">{success}</p>}
        </div>
        </div>
    );
};

export default SignupPage;
