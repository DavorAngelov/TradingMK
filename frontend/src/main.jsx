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

createRoot(document.getElementById('root')).render(

        <BrowserRouter>
            <Routes>
                <Route path="/dashboard" element={<Dashboard/>}></Route>
                <Route path="/landing" element={<LandingPage/>}></Route>
            </Routes>
        </BrowserRouter>

)
