import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import './index.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Menu from "./pages/Menu/Menu.jsx";
import AllocationSection from "./pages/Dashboard/AllocationSection/AllocationSection.jsx";
import Dashboard from "./pages/Dashboard/Dashboard.jsx";

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Dashboard/>}></Route>
            </Routes>
        </BrowserRouter>
    </StrictMode>,
)
