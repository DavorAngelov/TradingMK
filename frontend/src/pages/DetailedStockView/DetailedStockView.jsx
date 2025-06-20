import React, { useState } from 'react';
import { LineChart, Line, XAxis, YAxis, ResponsiveContainer } from 'recharts';
import { TrendingUp, Settings, Search, RotateCcw } from 'lucide-react';
import Menu from "../Menu/Menu.jsx";

const DetailedStockView = () => {
    const [selectedTimeframe, setSelectedTimeframe] = useState('24h');

    // Sample chart data
    const chartData = [
        { time: '8:00 AM', price: 3560 },
        { time: '10:00 AM', price: 3590 },
        { time: '12:00 PM', price: 3600 },
        { time: '2:00 PM', price: 3580 },
        { time: '4:00 PM', price: 3615 }
    ];


    return (
        <div className="min-h-screen bg-white text-gray-900">
            <Menu/>

            <div className="flex mt-20 border backdrop-blur-md shadow-lg rounded-xl bg-white/20 border-blue-100">
                {/* Main Chart Section */}
                <div className="flex-1 p-6">
                    {/* Token Header */}
                    <div className="flex items-center justify-between mb-6">
                        <div className="flex items-center space-x-4">
                            <div className="flex items-center space-x-2">
                                <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                                    <span className="text-white font-bold text-sm">Ξ</span>
                                </div>
                                <span className="text-xl font-semibold">ALK</span>
                            </div>
                        </div>
                        <div className="flex items-center space-x-4">
                            <TrendingUp className="w-5 h-5 text-gray-400" />
                        </div>
                    </div>

                    {/* Price Display */}
                    <div className="mb-6">
                        <div className="flex items-baseline space-x-4">
                            <span className="text-4xl font-bold">24.436 MKD</span>
                            <span className="text-green-500 font-medium">+3.27% today</span>
                        </div>
                    </div>

                    {/* Timeframe Selector */}
                    <div className="flex space-x-4 mb-6">
                        {['24h', '1w', '1m'].map((timeframe) => (
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

                    {/* Chart */}
                    <div className="h-80 mb-8">
                        <ResponsiveContainer width="100%" height="100%">
                            <LineChart data={chartData}>
                                <XAxis
                                    dataKey="time"
                                    axisLine={false}
                                    tickLine={false}
                                    tick={{ fill: '#6B7280', fontSize: 12 }}
                                />
                                <YAxis
                                    domain={['dataMin - 20', 'dataMax + 20']}
                                    axisLine={false}
                                    tickLine={false}
                                    tick={{ fill: '#6B7280', fontSize: 12 }}
                                />
                                <Line
                                    type="monotone"
                                    dataKey="price"
                                    stroke="#10B981"
                                    strokeWidth={2}
                                    dot={false}
                                    activeDot={{ r: 4, stroke: '#10B981', strokeWidth: 2 }}
                                />
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Right Sidebar */}
                <div className="w-80 border-l border-gray-200 p-6">
                    {/* ETH Balance */}
                    <div className="mb-6">
                        <div className="flex items-center justify-between mb-2">
                            <div className="flex items-center space-x-2">
                                <div className="w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center">
                                    <span className="text-white font-bold text-xs">Ξ</span>
                                </div>
                                <span className="font-medium">ALK</span>
                            </div>
                            <span className="text-sm text-gray-500">You Buy</span>
                        </div>
                        <input
                            type="text"
                            className="text-2xl font-bold mb-1"
                            value="2"
                        />

                    </div>

                    {/* Balance */}
                    <div className="mb-6">
                        <div className="flex items-center justify-between mb-2">
                            <div className="flex items-center space-x-2">
                                <div className="w-6 h-6 bg-green-500 rounded-full flex items-center justify-center">
                                    <span className="text-white font-bold text-xs">$</span>
                                </div>
                                <span className="font-medium">MKD</span>
                            </div>
                            <span className="text-sm text-gray-500">You Spend</span>
                        </div>
                        <div className="text-2xl font-bold mb-1">44.430</div>

                    </div>


                    <button className="w-full bg-gradient-to-r from-green-400 to-blue-500 text-white py-3 rounded-lg font-medium mb-4">
                        Buy
                    </button>



                    <div className="bg-gray-50 rounded-lg p-4">
                        <div className="text-sm text-gray-500 mb-2">Available Balance</div>
                        <div className="flex items-baseline space-x-2 mb-4">
                            <span className="text-xl font-bold">293.300 MKD</span>

                        </div>
                        <div className="grid grid-cols-3 gap-4 text-sm">
                            <div>
                                <div className="text-gray-500 mb-1">Estimate fee</div>
                                <div className="font-medium">430 MKD</div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DetailedStockView;