import React, {useEffect, useState} from "react";
import { TrendingUp, Wallet, PlusCircle } from "lucide-react";
import Menu from "../Menu/Menu.jsx";
import {useParams} from "react-router-dom";

const Portfolio = () => {
    const [selectedTimeframe, setSelectedTimeframe] = useState('1m');
    const {symbol} = useParams();
    const [chartData, setChartData] = useState([]);
    const [currentPrice, setCurrentPrice] = useState(null);
    const [percentage, setPercentage] = useState(null);

    useEffect(() => {
        if (!symbol || !selectedTimeframe) return;

        let from, to;
        const now = new Date();

        if (selectedTimeframe === '1w') {
            from = new Date(now);
            from.setDate(now.getDate() - 7); //1week
            to = now;
        } else if (selectedTimeframe === '1m') {
            from = new Date(now);
            from.setMonth(now.getMonth() - 1); // 1month
            to = now;
        }
        fetch(`http://localhost:8080/api/history/${symbol}?from=${from.toISOString().split('T')[0]}&to=${to.toISOString().split('T')[0]}`)
            .then(res => res.json())
            .then(data => {
                const sorted = data.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
                setChartData(sorted);
                console.log(sorted)
            })
            .catch(err => console.error("Fetch error:", err));

        fetch(`http://localhost:8080/api/stocks`)
            .then(res => res.json())
            .then(data => {
                const stock = data.find(stock => stock.symbol === symbol);
                setCurrentPrice(stock ? stock.currentPrice : null);
            })
            .catch(err => console.error("Error fetching current price:", err));
    }, [symbol,selectedTimeframe]);


    const calculatePercentageChange = (data) => {
        if (data.length < 2) return 0;
        const firstPrice = data[0].price;
        const lastPrice = data[data.length - 1].price;
        return ((lastPrice - firstPrice) / firstPrice) * 100;
    };

    useEffect(() => {
        if (chartData.length > 0) {
            const percentageChange = calculatePercentageChange(chartData);
            const formattedPercentageChange = percentageChange.toFixed(2);
            console.log(`Percentage Change: ${percentageChange.toFixed(2)}%`);
            setPercentage(formattedPercentageChange)
        }
    }, [chartData]);
    return (
        <div className=" max-w-7xl mx-auto space-y-8 pt-20  mb-4">

            <Menu/>



            <div className="flex items-center justify-between">
            <h3 className="text-4xl  text-gray-300 font-bold mb-8">My Portfolio</h3>

            <button className="flex items-center gap-2 bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg">
                <PlusCircle className="w-5 h-5" />
                Add Funds
            </button>
            </div>

            {/* wallet*/}
            <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm">
                <div className="flex flex-col space-y-1.5 p-6">
                    <div className="text-2xl font-light text-gray-600">Overview</div>
                </div>
                <div className="p-6 pt-0 grid grid-cols-1 md:grid-cols-2 gap-6">

                    <div className="flex flex-col">
                        <div className="text-sm text-gray-600">Wallet Balance</div>
                        <div className="flex items-baseline gap-3">
                            <span className="text-3xl font-bold text-gray-900">25,000 MKD</span>
                            <Wallet className="w-5 h-5 text-gray-500" />
                        </div>
                    </div>


                    <div className="flex flex-col">
                        <div className="text-sm text-gray-600">Invested in Stocks</div>
                        <div className="flex items-baseline gap-3">
                            <span className="text-3xl font-bold text-gray-900">47,990 MKD</span>
                            <TrendingUp className="w-5 h-5 text-green-500" />
                        </div>
                    </div>
                </div>
            </div>

            {/* performnce */}
            <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm">

                <div className="flex flex-col space-y-1.5 p-6 ">
                    <h3 className="text-xl font-semibold text-gray-800">Portfolio Performance</h3>
                    <div className="flex space-x-4 mb-6 mt-4">
                        {[ '1w', '1m'].map((timeframe) => (
                            <button
                                key={timeframe}
                                onClick={() => setSelectedTimeframe(timeframe)}
                                className={`px-3 py-1 rounded ${
                                    selectedTimeframe === timeframe
                                        ? 'bg-gray-900 text-white'
                                        : 'text-gray-500 hover:text-gray-700'
                                }`}
                            >
                                {timeframe}
                            </button>
                        ))}
                    </div>
                </div>
                <div className="p-6 pt-0">

                    <div className="h-48 bg-gray-50 rounded-lg flex items-center justify-center border-2 border-dashed border-gray-200">
                        <span className="text-gray-400">Performance Chart</span>
                    </div>


                    <div className="grid grid-cols-2 md:grid-cols-4 gap-6 pt-6">
                        <div>
                            <div className="text-sm text-gray-600">Total Profit</div>
                            <div className="font-semibold text-green-600">+7,200 MKD</div>
                            <div className="text-xs text-green-500">+15.81%</div>
                        </div>
                        <div>
                            <div className="text-sm text-gray-600">Best Stock</div>
                            <div className="font-semibold text-gray-900">Alkaloid</div>
                            <div className="text-xs text-gray-500">ALK</div>
                        </div>
                        <div>
                            <div className="text-sm text-gray-600">Worst Stock</div>
                            <div className="font-semibold text-gray-900">Komercijalna</div>
                            <div className="text-xs text-gray-500">KMB</div>
                        </div>
                        <div>
                            <div className="text-sm text-gray-600">Portfolio Score</div>
                            <div className="flex items-center gap-2">
                                <span className="bg-green-100 text-green-800 px-2 py-1 rounded-full font-medium">B</span>
                                <span className="text-sm text-gray-700">69 / 100</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* stocks grid */}
            <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm  ">
                <div className="flex flex-col space-y-1.5 p-6">
                    <h3 className="text-xl font-semibold text-gray-800">Stocks Breakdown</h3>
                </div>
                <div className="flex space-x-4 mb-6 mt-4 ml-6">
                    {[ '1w', '1m'].map((timeframe) => (
                        <button
                            key={timeframe}
                            onClick={() => setSelectedTimeframe(timeframe)}
                            className={`px-3 py-1 rounded ${
                                selectedTimeframe === timeframe
                                    ? 'bg-gray-900 text-white'
                                    : 'text-gray-500 hover:text-gray-700'
                            }`}
                        >
                            {timeframe}
                        </button>
                    ))}
                </div>
                <div className="p-6 pt-0 grid grid-cols-1 md:grid-cols-2 gap-6">
                    {[1, 2, 3].map((_, idx) => (
                        <div
                            key={idx}
                            className="p-4 bg-gray-50 rounded-lg border border-gray-200"
                        >

                            <div className="flex justify-between items-center mb-2">

                                <div>
                                    <div className="font-semibold text-gray-900">Stock {idx + 1}</div>
                                    <div className="text-xs text-gray-500">{idx + 1}</div>
                                </div>
                                <span className="bg-green-100 text-green-800 px-2 py-1 rounded text-xs font-medium">
                  +4.5%
                </span>
                            </div>

                            <div className="h-24 bg-white rounded flex items-center justify-center border-2 border-dashed border-gray-200">
                                <span className="text-gray-400">Stock Chart</span>
                            </div>
                        </div>
                    ))}
                </div>
            </div>



        </div>
    );
};

export default Portfolio;
