import React, {useEffect, useRef, useState} from "react";
import Menu from "../Menu/Menu.jsx";

const TransactionsPage = () => {
    const [transactions, setTransactions] = useState([]);
    const fileInputRef = useRef(null);

    const handleButtonClick = () => {
        fileInputRef.current.click(); // klik na inpiu
    };

    useEffect(() => {
        fetch("http://localhost:8080/api/transactions", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("accessToken")}`,
                "Content-Type": "application/json"
            }
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error("Failed to fetch transactions");
                }
                return res.json();
            })
            .then(data => setTransactions(data))
            .catch(err => console.error(err));
    }, []);

    const handleExport = () => {
        fetch("http://localhost:8080/api/transactions/export", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("accessToken")}`,
                "Content-Type": "application/json"
            }
        })
            .then(res => res.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(new Blob([blob]));
                const link = document.createElement("a");
                link.href = url;
                link.setAttribute("download", "transactions.csv");
                document.body.appendChild(link);
                link.click();
            });
    };




    const loadTransactions = () => {
        fetch("http://localhost:8080/api/transactions", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("accessToken")}`,
                "Content-Type": "application/json"
            }
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error("transactions fail");
                }
                return res.json();
            })
            .then(data => setTransactions(data))
            .catch(err => console.error(err));
    };

    useEffect(() => {
        loadTransactions();
    }, []);

    const handleImport = async (e) => {
        const file = e.target.files[0];
        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await fetch("http://localhost:8080/api/transactions/import", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
                },
                body: formData,
            });

            if (!response.ok) {
                throw new Error("import fail");
            }

            alert("Import successful!");
            loadTransactions();

        } catch (err) {
            console.error(err);
            alert("import fail");
        } finally {
            e.target.value = "";
        }
    };

    return (
        <div className=" max-w-7xl mx-auto space-y-8 pt-20  mb-4">
            <Menu/>
            <h3 className="text-4xl  text-gray-300 font-bold mb-8">Transactions</h3>

            <div className="flex gap-4 mb-4">
                <button onClick={handleExport} className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg cursor-pointer">
                    Export CSV
                </button>

                <button onClick={handleButtonClick} className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg cursor-pointer">
                    Import CSV
                </button>
                <input
                    type="file"
                    accept=".csv"
                    ref={fileInputRef}
                    style={{ display: "none" }}
                    onChange={handleImport}
                />

            </div>

            <table className="min-w-full border border-gray-300">
                <thead>
                <tr className="bg-gray-100">
                    <th className="border px-2 py-1">ID</th>
                    <th className="border px-2 py-1">Stock</th>
                    <th className="border px-2 py-1">Type</th>
                    <th className="border px-2 py-1">Quantity</th>
                    <th className="border px-2 py-1">Price</th>
                    <th className="border px-2 py-1">Timestamp</th>
                </tr>
                </thead>
                <tbody>
                {transactions.map(t => (
                    <tr key={t.id}>
                        <td className="border px-2 py-1">{t.id}</td>
                        <td className="border px-2 py-1">{t.stock.symbol}</td>
                        <td className="border px-2 py-1">{t.type}</td>
                        <td className="border px-2 py-1">{t.quantity}</td>
                        <td className="border px-2 py-1">{t.price}</td>
                        <td className="border px-2 py-1">{t.timestamp}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default TransactionsPage;
