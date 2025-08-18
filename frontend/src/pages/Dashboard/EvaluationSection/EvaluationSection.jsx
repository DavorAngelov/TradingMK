import React, {useEffect, useState} from 'react';
import {TrendingUp, Wallet} from 'lucide-react';

const EvaluationSection = () => {
    const [currentPrices, setCurrentPrices] = useState({});
    const [percentage, setPercentage] = useState(null);
    const [portfolio, setPortfolio] = useState({balance: 0, holdings: []});

    useEffect(() => {
        const isDemo = localStorage.getItem("demo") === "true";
        if (isDemo) {
            const demoPortfolio = JSON.parse(localStorage.getItem("demoPortfolio") || '{}');
            setPortfolio(demoPortfolio);


            // fetch stock prices for holdings
            const symbols = demoPortfolio.holdings.map(h => h.stockSymbol);
            if (symbols.length > 0) {
                fetch("http://localhost:8080/api/stocks")
                    .then(res => res.json())
                    .then(allStocks => {
                        const priceMap = {};
                        symbols.forEach(symbol => {
                            const stock = allStocks.find(s => s.symbol === symbol);
                            priceMap[symbol] = stock ? stock.currentPrice : null;
                        });
                        setCurrentPrices(priceMap);
                    });
            }

        } else{
        fetch("http://localhost:8080/api/portfolio", {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            }
        })
            .then(async res => {
                const text = await res.text();  // raw
                console.log("Raw portfolio response:", text);

                try {
                    const data = JSON.parse(text);
                    setPortfolio(data);

                    const symbols = data.holdings.map(h => h.stockSymbol);
                    if (symbols.length === 0) return;

                    fetch("http://localhost:8080/api/stocks")
                        .then(res => res.json())
                        .then(allStocks => {
                            const priceMap = {};
                            symbols.forEach(symbol => {
                                const stock = allStocks.find(s => s.symbol === symbol);
                                priceMap[symbol] = stock ? stock.currentPrice : null;
                            });
                            setCurrentPrices(priceMap);
                        })
                        .catch(err => console.error("dsad", err));
                } catch (err) {
                    console.error("Invalid JSON from backend", err);
                }
            })
            .catch(err => console.error("error", err));
    }}, []);


    const investedInStocks = portfolio.holdings.reduce((sum, holding) => {
        return sum + holding.quantity * Number(holding.avgPrice);
    }, 0);

    //FOR PERFORAMNFCE
    const currentValue = portfolio.holdings.reduce((sum, h) => {
        const currentPrice = currentPrices[h.stockSymbol] || 0;
        return sum + h.quantity * currentPrice;
    }, 0);

    const totalProfit = currentValue - investedInStocks;
    const totalProfitPercent = investedInStocks === 0 ? 0 : (totalProfit / investedInStocks) * 100;

    let bestStock = null;
    let bestProfitPercent = -Infinity;
    let worstStock = null;
    let worstProfitPercent = Infinity;

    portfolio.holdings.forEach(h => {
        const currentPrice = currentPrices[h.stockSymbol] || 0;
        if (h.avgPrice === 0) return;
        const profitPercent = ((currentPrice - h.avgPrice) / h.avgPrice) * 100;
        if (profitPercent > bestProfitPercent) {
            bestProfitPercent = profitPercent;
            bestStock = h.stockSymbol;
        }
        if (profitPercent < worstProfitPercent) {
            worstProfitPercent = profitPercent;
            worstStock = h.stockSymbol;
        }
    });
    return (
        <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm">
            <div className="flex flex-col space-y-1.5 p-6">
                <h3 className="text-xl font-semibold text-gray-800">Evaluation</h3>
                <div className="text-sm text-gray-600">Total assets</div>
            </div>
            <div className="p-6 pt-0">
                <div className="space-y-6">
                    <div className="flex justify-around">
                        <div className="flex flex-col">
                            <div className="text-sm text-gray-600">Wallet Balance</div>
                            <div className="flex items-baseline gap-3">
                                <span
                                    className="text-3xl font-bold text-gray-900">{portfolio.balance.toLocaleString()} MKD</span>
                                <Wallet className="w-5 h-5 text-gray-500"/>
                            </div>
                        </div>

                        <div className="flex flex-col">
                            <div className="text-sm text-gray-600">Invested in Stocks</div>
                            <div className="flex items-baseline gap-3">
                                <span
                                    className="text-3xl font-bold text-gray-900">{investedInStocks.toLocaleString(undefined, {maximumFractionDigits: 2})} MKD</span>

                            </div>
                        </div>
                    </div>
                    {/*<div className="flex items-center gap-2 text-sm">*/}
                    {/*    <span className="text-gray-600">Strong performance</span>*/}
                    {/*    <TrendingUp className="w-4 h-4 text-green-500"/>*/}
                    {/*</div>*/}




                    <div className="grid grid-cols-2 md:grid-cols-4 gap-6 pt-6 justify-center border-t border-gray-100">
                        <div>
                            <div className="text-sm text-gray-600">Total Profit</div>
                            <div className={`font-semibold ${totalProfit >= 0 ? "text-green-600" : "text-red-600"}`}>
                                {totalProfit >= 0 ? "+" : "-"}{Math.abs(totalProfit).toFixed(2)} MKD
                            </div>
                            <div className={`text-xs ${totalProfitPercent >= 0 ? "text-green-500" : "text-red-500"}`}>
                                {totalProfitPercent >= 0 ? "+" : "-"}{Math.abs(totalProfitPercent).toFixed(2)}%
                            </div>
                        </div>
                        <div>
                            <div className="text-sm text-gray-600">Best Stock</div>
                            <div className="font-semibold text-gray-900">{bestStock || "N/A"}</div>
                            <div className="text-xs text-gray-500">{bestStock || ""}</div>
                        </div>
                        <div>
                            <div className="text-sm text-gray-600">Worst Stock</div>
                            <div className="font-semibold text-gray-900">{worstStock || "N/A"}</div>
                            <div className="text-xs text-gray-500">{worstStock || ""}</div>
                        </div>

                    </div>


                    <div className="grid grid-cols-1 gap-4 pt-4 border-t border-gray-100">
                        {/*<div className="text-center">*/}
                        {/*    <div className="text-sm text-gray-600 mb-2">Portfolio score</div>*/}
                        {/*    <div*/}
                        {/*        className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-1">*/}
                        {/*        <span className="text-green-800 font-bold">B</span>*/}
                        {/*    </div>*/}
                        {/*    <div className="font-bold text-2xl">69</div>*/}
                        {/*    <div className="text-sm text-gray-500">/100</div>*/}
                        {/*    <div className="text-xs text-green-600">Good</div>*/}
                        {/*</div>*/}

                    </div>
                </div>
            </div>
        </div>
    );
};

export default EvaluationSection;