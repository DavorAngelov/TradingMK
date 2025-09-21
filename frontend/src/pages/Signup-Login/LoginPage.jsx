import React, {useState} from 'react';
import {useNavigate} from "react-router-dom";
import {GoogleLogin} from "@react-oauth/google";

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const login = async () => {
        const response = await fetch('http://localhost:8080/api/auth/authenticate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        const data = await response.json();
        const token = data.token;


        localStorage.setItem('accessToken', token);

        //decode
        try {
            const { jwtDecode } = await import('jwt-decode');
            const decoded = jwtDecode(token);
            localStorage.setItem('username', decoded.sub); // sub contains usernamere
            localStorage.setItem('role', decoded.role || 'USER');
        } catch (err) {
            console.error('Failed to decode token:', err);
        }

        return "Login successful!";
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            const message = await login();
            setSuccess(message);

            // redirect based on role
            const role = localStorage.getItem('role');
            if (role === 'ADMIN') {
                navigate('/admin');      // admins go to admin page
            } else {
                navigate('/dashboard');  // normal users go to dashboard
            }

        } catch (err) {
            setError(err.message || 'Login failed');
        }
    };






    const handleGoogleLogin = async (credentialResponse) => {
        try {
            const token = credentialResponse.credential; // ID token
            const res = await fetch('http://localhost:8080/api/auth/google', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ credential: token })
            });

            if (!res.ok) {
                const msg = await res.text();
                alert(msg);
                return;
            }

            const data = await res.json();
            localStorage.setItem('accessToken', data.token);
            alert('Google login successful!');
            navigate('/dashboard');
        } catch (err) {
            console.error(err);
            alert('Login failed');
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
                <h2 className="text-2xl mb-4 font-semibold">Log In</h2>
                <form onSubmit={handleLogin} className="space-y-4">
                    <input
                        type="text"
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                        placeholder="Username"
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
                        Log In
                    </button>
                </form>
                {error && <p className="text-red-600 mt-4">{error}</p>}
                {success && <p className="text-green-600 mt-4">{success}</p>}

                    <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow text-center">
                        <h2 className="text-2xl mb-4 font-semibold">Login with Google</h2>
                        <GoogleLogin
                            onSuccess={handleGoogleLogin}
                            onError={() => {
                                console.log('Login Failed');
                            }}
                        />
                    </div>
            </div>



        </div>
    );
};

export default LoginPage;
