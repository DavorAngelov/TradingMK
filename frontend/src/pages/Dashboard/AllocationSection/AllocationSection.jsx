import React, {useEffect, useState} from 'react';
import {Link} from "react-router-dom";

const AllocationSection = () => {
    // const allocations = [
    //     {
    //         name: 'Комерцијална банка АД Скопје',
    //         symbol: 'KMB',
    //         percentage: -1.04,
    //         amount: '27.065,02',
    //         color: 'bg-red-300'
    //     },
    //     {name: 'Алкалоид АД Скопје', symbol: 'ALK', percentage: -1.48, amount: '24.469,01', color: 'bg-red-300'},
    //     {name: 'Макпетрол АД Скопје', symbol: 'MPT', percentage: -1.35, amount: '109.500,00', color: 'bg-red-300'},
    //     {name: 'Реплек АД Скопје', symbol: 'REPL', percentage: 0.01, amount: '1.799,56', color: 'bg-green-300'},
    //     {name: 'Гранит АД Скопје', symbol: 'GRNT', percentage: 0.01, amount: '1.799,56', color: 'bg-green-300'},
    //     {name: 'Македонски Телеком АД – Скопје', symbol: 'TEL', percentage: 0.36, amount: '440,00',color: 'bg-green-300'},
    //     {name: 'НЛБ Банка АД Скопје', symbol: 'TNB', percentage: 0.00, amount: '57.960,00',color:'bg-gray-200'},
    // ];

    const [stocks, setStocks] = useState([]);

    useEffect(() => {
        fetch("http://localhost:8080/api/stocks")
            .then((res) => res.json())
            .then((data) => {
                const sortedData = data.sort((a, b) => new Date(b.lastUpdated) - new Date(a.lastUpdated));
                setStocks(sortedData);
            })
            .catch((err) => console.error("Error fetching stocks:", err));
    }, []);

    return (
        <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm">
            <div className="flex flex-row justify-between space-y-1.5 p-6">
                <h3 className="text-xl font-semibold text-gray-800 ">MBI10 Elements</h3>
                    <h3 className=""><span className="w-2 h-2 bg-blue-400 rounded-full inline-block mr-2"></span>Last update of the price</h3>
            </div>
            <div className="p-6 pt-0">
                <div className="space-y-4 ">
                    {/* Main allocation blocks */}
                    <div className="grid grid-cols-2 gap-2 ">
                        {stocks.map((token) => (
                            <Link to={`/detailed/${token.symbol}`}
                                  key={token.symbol}
                                  className={`${token.percentage < 0 ? 'bg-red-300' : token.percentage > 0 ? 'bg-green-300' : 'bg-gray-200'} p-4 rounded-lg text-gray-800 'h-24'`}
                            >
                                <div className="flex items-center gap-2 mb-2">
                                    <div className="w-7 h-7 bg-black rounded-full flex items-center justify-center">
                    <span className="text-white text-xs font-bold">
                      {token.symbol}
                    </span>
                                    </div>
                                    <div>
                                        <div className="font-medium text-sm">{token.name}</div>
                                        <div className="text-xs opacity-75">{token.symbol}</div>

                                    </div>
                                    <div className="text-xs text-gray-600 border-b-2 border-blue-400 rounded-md ml-auto">
                                        {new Date(token.lastUpdated).toLocaleDateString([], {
                                            year: 'numeric',
                                            month: '2-digit',
                                            day: '2-digit'
                                        })}{' '}
                                        {new Date(token.lastUpdated).toLocaleTimeString([], {
                                            hour: '2-digit',
                                            minute: '2-digit'
                                        })}
                                    </div>
                                </div>



                                <div className="flex justify-between items-end">
                                    <span className="font-bold text-lg">{token.currentPrice} MKD</span>
                                    <span className="text-sm">{token.percentage}%</span>
                                </div>
                            </Link>
                        ))}
                    </div>


                </div>
            </div>
        </div>
    );
};

export default AllocationSection;