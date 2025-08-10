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

createRoot(document.getElementById('root')).render(

        <BrowserRouter>
            <Routes>
                <Route path="/dashboard" element={<Dashboard/>}></Route>
                <Route path="/" element={<LandingPage/>}></Route>
                <Route path="/detailed/:symbol" element={<DetailedStockView/>}></Route>
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/portfolio" element={<Portfolio />} />
            </Routes>
        </BrowserRouter>

)
