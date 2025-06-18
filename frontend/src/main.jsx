import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import './index.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Menu from "./pages/Menu/Menu.jsx";

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Menu/>}></Route>
            </Routes>
        </BrowserRouter>
    </StrictMode>,
)
