// Watchlist.jsx
import React, { useEffect, useState } from "react";
import Menu from "../Menu/Menu.jsx";
import {PlusCircle, TrashIcon} from "lucide-react";

export default function Watchlist() {
    const [watchlist, setWatchlist] = useState([]);
    const token = localStorage.getItem("accessToken");

    const fetchWatchlist = async () => {
        if (!token) return;

        try {
            const res = await fetch("http://localhost:8080/api/watchlist", {
                headers: { "Authorization": `Bearer ${token}` },
            });
            const data = await res.json();
            setWatchlist(data);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchWatchlist();
    }, []);

    const handleDelete = async (id) => {
        try {
            await fetch(`http://localhost:8080/api/watchlist/${id}`, {
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` },
            });
            //set weath
            setWatchlist(watchlist.filter((entry) => entry.id !== id));
        } catch (err) {
            console.error("Failed to delete watchlist entry", err);
        }
    };

    return (
        <div className=" max-w-7xl mx-auto space-y-8 pt-20  mb-4">
            <Menu />
                <div className="flex items-center justify-between">
                    <h3 className="text-4xl  text-gray-300 font-bold ">Watchlist</h3>
                </div>

                <div className="flex flex-col mt-10 mx-6 border backdrop-blur-md shadow-lg rounded-xl bg-white/20 border-blue-100 p-6">
                {watchlist.length === 0 ? (
                    <p className="text-gray-500">No stocks in your watchlist.</p>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {watchlist.map((entry) => (
                            <div
                                key={entry.id}
                                className="flex flex-col justify-between bg-white/80 backdrop-blur-md rounded-xl p-4 shadow hover:shadow-lg transition"
                            >
                                <div className="flex items-center justify-between mb-2">
                                    <div className="flex items-center space-x-2">
                                        <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                                            <span className="text-white font-bold text-sm">Ξ</span>
                                        </div>
                                        <span className="text-lg font-semibold">{entry.stock.symbol}</span>
                                    </div>
                                    <button onClick={() => handleDelete(entry.id)}>
                                        <TrashIcon className="w-5 h-5 text-red-500 hover:text-red-700 cursor-pointer" />
                                    </button>
                                </div>

                                <div className="text-gray-700">
                                    <p>
                                        <span className="font-medium">Price Above:</span> {entry.priceAbove ?? "-"}
                                    </p>
                                    <p>
                                        <span className="font-medium">Price Below:</span> {entry.priceBelow ?? "-"}
                                    </p>
                                    <p>
                                        <span className="font-medium">Current Price:</span> {entry.stock.currentPrice}
                                    </p>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}