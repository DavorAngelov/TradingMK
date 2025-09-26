import React, {useEffect, useState} from 'react';
import {LineChart, Line, XAxis, YAxis, ResponsiveContainer} from 'recharts';
import {TrendingUp, Settings, Search, RotateCcw} from 'lucide-react';
import Menu from "../Menu/Menu.jsx";
import {useParams} from "react-router-dom";

const DetailedStockView = () => {
    const [selectedTimeframe, setSelectedTimeframe] = useState('1m');
    const {symbol} = useParams();
    const [chartData, setChartData] = useState([]);
    const [currentPrice, setCurrentPrice] = useState(null);
    const [percentage, setPercentage] = useState(null);

    const [buyQuantity, setBuyQuantity] = useState(0);
    const [buyTotal, setBuyTotal] = useState(0);
    const [availableBalance, setAvailableBalance] = useState(0);

    const [portfolio, setPortfolio] = useState(null);

    const [showWatchlistPopup, setShowWatchlistPopup] = useState(false);
    const [priceAbove, setPriceAbove] = useState("");
    const [priceBelow, setPriceBelow] = useState("");
    const [selectedStockId, setSelectedStockId] = useState(null);


    useEffect(() => {
        const isDemo = localStorage.getItem("demo") === "true";
        if (isDemo) {
            const storedPortfolio = JSON.parse(localStorage.getItem("demoPortfolio"));
            setPortfolio(storedPortfolio);
            setAvailableBalance(storedPortfolio?.balance || 0);
        } else {
            fetch('http://localhost:8080/api/portfolio', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
                }
            })
                .then(res => res.json())
                .then(data => setPortfolio(data))
                .catch(err => console.error(err));
        }
    }, []);

    const portfolioId = portfolio?.id;

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
    }, [symbol, selectedTimeframe]);


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

    const handleBuy = async () => {
        const isDemo = localStorage.getItem("demo") === "true";
        if (!buyQuantity || buyQuantity <= 0) {
            alert("Enter a valid quantity");
            return;
        }

        if (isDemo) {
            const storedPortfolio = JSON.parse(localStorage.getItem("demoPortfolio")) || { balance: 100000, holdings: [] };
            const totalCost = buyQuantity * currentPrice;
            if (storedPortfolio.balance < totalCost) {
                alert("Not enough balance for demo purchase");
                return;
            }
            storedPortfolio.balance -= totalCost;
            const existingHolding = storedPortfolio.holdings.find(h => h.stockSymbol === symbol);
            if (existingHolding) {
                const newQuantity = existingHolding.quantity + buyQuantity;
                existingHolding.avgPrice = ((existingHolding.avgPrice * existingHolding.quantity) + (currentPrice * buyQuantity)) / newQuantity;
                existingHolding.quantity = newQuantity;
            } else {
                storedPortfolio.holdings.push({
                    stockSymbol: symbol,
                    quantity: buyQuantity,
                    avgPrice: currentPrice
                });
            }
            localStorage.setItem("demoPortfolio", JSON.stringify(storedPortfolio));
            setPortfolio(storedPortfolio);
            setAvailableBalance(storedPortfolio.balance);
            alert("Demo stock purchased successfully!");
            setBuyQuantity(0);
            setBuyTotal(0);
        } else {
            const token = localStorage.getItem("accessToken");
            if (!token) {
                alert("You must be logged in to buy stocks");
                return;
            }
            try {
                const response = await fetch("http://localhost:8080/api/trades/request", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        stockSymbol: symbol,
                        quantity: parseInt(buyQuantity),
                        pricePerUnit: currentPrice,
                        type: "BUY"
                    })
                });

                if (!response.ok) {
                    const text = await response.text();
                    alert("Failed to request trade: " + text);
                    return;
                }

                alert("Trade request sent! Waiting for broker approval.");
                setBuyQuantity("");
            } catch (err) {
                console.error("Error sending trade request:", err);
                alert("Network error");
            }
        }
    };


    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        const isDemo = localStorage.getItem("demo") === "true";

        if (isDemo) {
            const storedPortfolio = JSON.parse(localStorage.getItem("demoPortfolio")) || {
                balance: 100000,
                holdings: []
            };
            setAvailableBalance(storedPortfolio.balance);
            setPortfolio(storedPortfolio);
        }else{

        fetch("http://localhost:8080/api/portfolio", {
            headers: {Authorization: `Bearer ${token}`}
        })
            .then(res => res.json())
            .then(data => setAvailableBalance(data.balance))
            .catch(err => console.error("Error fetching balance", err));
    }}, []);

    const handleAddToWatchlist = async () => {
        const token = localStorage.getItem("accessToken");
        console.log("Token:", token);
        if (!token) {
            alert("loggin to use watchlist");
            return;
        }

        try {
            await fetch("http://localhost:8080/api/watchlist", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({
                    symbol: symbol,
                    priceAbove: priceAbove ? parseFloat(priceAbove) : null,
                    priceBelow: priceBelow ? parseFloat(priceBelow) : null
                })
            });

            alert(`Stock ${symbol} added to watchlist!`);
            setShowWatchlistPopup(false);
            setPriceAbove("");
            setPriceBelow("");
        } catch (err) {
            console.error(err);
            alert("failed to add ");
        }
    };



    return (
        <div className="min-h-screen bg-white text-gray-900  mb-4">
            <Menu/>

            <div className="flex mt-20 border backdrop-blur-md shadow-lg rounded-xl bg-white/20 border-blue-100">
                <div className="flex-1 p-6">

                    <div className="flex items-center justify-between mb-6">
                        <div className="flex items-center space-x-4">
                            <div className="flex items-center space-x-2">
                                <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                                    <span className="text-white font-bold text-sm">Ξ</span>
                                </div>
                                <span className="text-xl font-semibold">{symbol}</span>
                            </div>
                        </div>
                        <div className="flex items-center space-x-4">
                            <TrendingUp className="w-5 h-5 text-gray-400"/>
                        </div>
                    </div>


                    <div className="mb-6">
                        <div className="flex items-baseline space-x-4">
                            <span className="text-4xl font-bold">{currentPrice}</span>
                            <span
                                className={`${percentage < 0 ? 'text-red-400' : percentage > 0 ? 'text-green-400' : 'text-gray-200'} font-medium`}>
    {percentage !== null ? `${percentage} %` : ''}
</span>
                        </div>
                    </div>


                    <div className="flex space-x-4 mb-6">
                        {['1w', '1m'].map((timeframe) => (
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
                                    tick={{fill: '#6B7280', fontSize: 12}}
                                />
                                <YAxis
                                    domain={['dataMin - 20', 'dataMax + 20']}
                                    axisLine={false}
                                    tickLine={false}
                                    tick={{fill: '#6B7280', fontSize: 12}}
                                />
                                <Line
                                    type="monotone"
                                    dataKey="price"
                                    stroke="#10B981"
                                    strokeWidth={2}
                                    dot={false}
                                    activeDot={{r: 4, stroke: '#10B981', strokeWidth: 2}}
                                />
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Right Sidebar */}
                <div className="w-80 border-l border-gray-200 p-6">

                    <div className="mb-6">
                        <div className="flex items-center justify-between mb-2">
                            <div className="flex items-center space-x-2">
                                <div className="w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center">
                                    <span className="text-white font-bold text-xs">Ξ</span>
                                </div>
                                <span className="font-medium">{symbol}</span>
                            </div>
                            <span className="text-sm text-gray-500">You Buy</span>

                        </div>
                        <input
                            type="number"
                            min="0"
                            className="text-2xl font-bold mb-1"
                            value={buyQuantity}
                            onChange={(e) => {
                                const qty = Number(e.target.value);
                                setBuyQuantity(qty);
                                setBuyTotal(qty * (currentPrice || 0));
                            }}
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
                        <div className="text-2xl font-bold mb-1">{buyTotal.toFixed(2)}</div>

                    </div>


                    <button
                        onClick={handleBuy}
                        className="w-full bg-gradient-to-r from-green-400 to-blue-500 text-white py-3 rounded-lg font-medium mb-4"
                    >
                        Buy
                    </button>


                    <div className="bg-gray-50 rounded-lg p-4">
                        <div className="text-sm text-gray-500 mb-2">Available Balance</div>
                        <div className="flex items-baseline space-x-2 mb-4">
                            <span className="text-xl font-bold">{availableBalance.toFixed(2)} MKD</span>

                        </div>
                        <div className="grid grid-cols-3 gap-4 text-sm">
                            <div>
                                <div className="text-gray-500 mb-1">Estimate fee</div>
                                <div className="font-medium">0 MKD</div>
                            </div>

                        </div>
                    </div>


                    <button
                        onClick={() => setShowWatchlistPopup(true)}
                        className="w-full bg-yellow-400 text-white py-3 rounded-lg font-medium mb-4 mt-4 cursor-pointer"
                    >
                        Add to Watchlist
                    </button>

                    {showWatchlistPopup && (
                        <div className="fixed inset-0 bg-black/30 flex items-center justify-center z-50">
                            <div className="bg-white p-6 rounded-lg w-80">
                                <h3 className="text-lg font-semibold mb-4">{symbol} - Set Alert Prices</h3>
                                <input
                                    type="number"
                                    placeholder="Price Above"
                                    className="w-full mb-2 p-2 border rounded"
                                    value={priceAbove}
                                    onChange={(e) => setPriceAbove(e.target.value)}
                                />
                                <input
                                    type="number"
                                    placeholder="Price Below"
                                    className="w-full mb-4 p-2 border rounded"
                                    value={priceBelow}
                                    onChange={(e) => setPriceBelow(e.target.value)}
                                />
                                <div className="flex justify-end space-x-2">
                                    <button
                                        onClick={handleAddToWatchlist}
                                        className="bg-green-500 text-white px-4 py-2 rounded"
                                    >
                                        Save
                                    </button>
                                    <button
                                        onClick={() => setShowWatchlistPopup(false)}
                                        className="bg-gray-300 text-gray-700 px-4 py-2 rounded"
                                    >
                                        Cancel
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}

                </div>
            </div>
        </div>
    );
};

export default DetailedStockView;