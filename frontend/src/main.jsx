import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import './index.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Menu from "./pages/Menu/Menu.jsx";
import AllocationSection from "./pages/Dashboard/AllocationSection/AllocationSection.jsx";
import Dashboard from "./pages/Dashboard/Dashboard.jsx";
import LandingPage from "./pages/LandingPage/LandingPage.jsx";
import DetailedStockView from "./pages/DetailedStockView/DetailedStockView.jsx";
import SignupPage from "./pages/Signup-Login/SignupPage.jsx";
import LoginPage from "./pages/Signup-Login/LoginPage.jsx";
import Portfolio from "./pages/Portfolio/Portfolio.jsx";
import Education from "./pages/Education/Education.jsx";
import Settings from "./pages/Settings/Settings.jsx";
import AdminTrades from "./pages/AdminTrades/AdminTrades.jsx";
import {GoogleOAuthProvider} from "@react-oauth/google";
import Watchlist from "./pages/Watchlist/Watchlist.jsx";



createRoot(document.getElementById('root')).render(
    <StrictMode>
        <GoogleOAuthProvider clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID}>
            <BrowserRouter>
                <Routes>
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/" element={<LandingPage />} />
                    <Route path="/detailed/:symbol" element={<DetailedStockView />} />
                    <Route path="/signup" element={<SignupPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/portfolio" element={<Portfolio />} />
                    <Route path="/education" element={<Education />} />
                    <Route path="/settings" element={<Settings />} />
                    <Route path="/admin" element={<AdminTrades />} />
                    <Route path="/watchlist" element={<Watchlist />} />
                </Routes>
            </BrowserRouter>
        </GoogleOAuthProvider>
    </StrictMode>
)
