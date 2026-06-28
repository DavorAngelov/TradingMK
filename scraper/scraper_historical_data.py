import requests
import os
from bs4 import BeautifulSoup
from datetime import datetime, timedelta

BACKEND_URL = os.getenv("BACKEND_URL", "http://backend-service.tradingmk.svc.cluster.local:8080")

def post_history_for_all():
    symbols = ["KMB","ALK","TEL","REPL","TNB","PPIV","UNI","STB","MPT","TTK","GRNT","MTUR"]
    for s in symbols:
        post_history_to_backend(s)

def get_stock_history(symbol, days=30):
    url = f"https://www.mse.mk/en/stats/symbolhistory/{symbol}"
    response = requests.get(url)
    soup = BeautifulSoup(response.text, "html.parser")

    table = soup.select_one(".table")
    if not table:
        return []

    cutoff = datetime.now() - timedelta(days=days)
    data = []

    for row in table.select("tbody tr"):
        cols = row.find_all("td")
        if len(cols) < 5:
            continue
        try:
            date = datetime.strptime(cols[0].text.strip(), "%m/%d/%Y")
            if date < cutoff:
                break
            data.append({
                "stock": {"symbol": symbol},
                "timestamp": date.strftime("%Y-%m-%d"),
                "price": float(cols[4].text.strip().replace(",", ""))
            })
        except:
            continue

    return data

def post_history_to_backend(symbol):
    data = get_stock_history(symbol)
    if data:
        requests.post(f"{BACKEND_URL}/api/history/upload", json=data)
        print(f"Uploaded {symbol}")