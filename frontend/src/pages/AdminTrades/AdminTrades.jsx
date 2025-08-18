import React, { useEffect, useState } from "react";
import {LogOut} from "lucide-react";
import {useNavigate} from "react-router-dom";
import Menu from "../Menu/Menu.jsx";

const AdminTrades = () => {
    const navigate = useNavigate();
    const [pendingTrades, setPendingTrades] = useState([]);
    const [username, setUsername] = useState("");
    const fetchPending = () => {
        const token = localStorage.getItem("accessToken");
        const storedUsername = localStorage.getItem("username");
        setUsername(storedUsername);
        if (!token) {
            alert("You must be logged in as admin to view trades");
            return;
        }

        fetch("http://localhost:8080/api/trades/pending", {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        })
            .then(res => {
                if (!res.ok) return res.text().then(text => { throw new Error(text) });
                return res.json();
            })
            .then(data => setPendingTrades(data))
            .catch(err => console.error("Error fetching pending trades:", err));
    };

    useEffect(() => {
        fetchPending();
    }, []);

    const handleAction = (id, action) => {
        const token = localStorage.getItem("accessToken");
        if (!token) {
            alert("You must be logged in as admin to perform this action");
            return;
        }

        fetch(`http://localhost:8080/api/trades/${id}/${action}`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        })
            .then(res => {
                if (!res.ok) return res.text().then(text => { throw new Error(text) });
                return res.json();
            })
            .then(() => fetchPending())
            .catch(err => alert("Error performing action: " + err.message));
    };
    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("username");
        navigate("/");
    };


    return (
        <div className="p-6">

            <div className="flex justify-between items-center mb-6">
                <div className="text-xl font-bold">Welcome to the Admin Page, {username}</div>
                <button
                    onClick={handleLogout}
                    className="flex items-center gap-2 bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg cursor-pointer"
                >
                    <LogOut className="w-5 h-5" />
                    Logout
                </button>
            </div>

            <h2 className="text-lg font-semibold mb-4">Pending Trade Requests</h2>

            <div className="space-y-4">
                {pendingTrades.length === 0 ? (
                    <div className="text-gray-500">No pending trades</div>
                ) : (
                    pendingTrades.map(trade => (
                        <div key={trade.id} className="border p-4 rounded flex justify-between items-center">
                            <div>
                                <div>{trade.type} {trade.quantity} of {trade.stockSymbol} at {trade.pricePerUnit}</div>
                                <div className="text-sm text-gray-500">{trade.timestamp}</div>
                            </div>
                            <div className="flex space-x-2">
                                <button
                                    className="bg-green-500 text-white px-2 py-1 rounded cursor-pointer"
                                    onClick={() => handleAction(trade.id, "approve")}
                                >
                                    Approve
                                </button>
                                <button
                                    className="bg-red-500 text-white px-2 py-1 rounded cursor-pointer"
                                    onClick={() => handleAction(trade.id, "decline")}
                                >
                                    Decline
                                </button>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default AdminTrades;
