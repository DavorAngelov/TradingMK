import React, {useEffect, useState} from 'react';
import { ChevronRight } from 'lucide-react';

const BreakdownSection = () => {
    const [stocks, setStocks] = useState([]);

    useEffect(() => {
        fetch("http://localhost:8080/api/stocks")
            .then((res) => res.json())
            .then((data) => setStocks(data))
            .catch((err) => console.error("Error fetching stocks:", err));
    }, []);



    return (
        <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm">
            <div className="flex flex-col space-y-1.5 p-6">
                <h3 className="text-xl font-semibold text-gray-800">Breakdown</h3>
            </div>
            <div className="p-6 pt-0">
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead>
                        <tr className="text-left text-sm text-gray-600 border-b border-gray-200">
                            <th className="pb-3 font-medium">Token</th>
                            <th className="pb-3 font-medium text-right">Turnover in BEST</th>
                            <th className="pb-3 font-medium text-right">%</th>
                            <th className="pb-3 font-medium text-right">Price</th>
                            <th className="pb-3 w-8"></th>
                        </tr>
                        </thead>
                        <tbody>
                        {stocks.map((token) => (
                            <tr key={token.symbol} className="border-b border-gray-100 hover:bg-gray-50 transition-colors">
                                <td className="py-4">
                                    <div className="flex items-center gap-3">
                                        <div className="w-8 h-8 bg-gray-800 rounded-full flex items-center justify-center">
                                            <span className="text-white text-sm font-bold">{token.symbol}</span>
                                        </div>
                                        <div>
                                            <div className="font-medium text-gray-900">{token.name}</div>
                                            <div className="text-sm text-gray-500">{token.symbol}</div>
                                        </div>
                                    </div>
                                </td>
                                <td className="py-4 text-right font-medium text-gray-900">{token.turnover}</td>
                                <td className="py-4 text-right">
                    <span className="bg-gray-100 text-gray-800 px-2 py-1 rounded text-sm">
                       {token.percentage} %
                    </span>
                                </td>
                                <td className="py-4 text-right font-medium text-gray-900">{token.currentPrice}</td>
                                <td className="py-4">
                                    <ChevronRight className="w-4 h-4 text-gray-400" />
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default BreakdownSection;