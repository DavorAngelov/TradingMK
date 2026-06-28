import requests
from bs4 import BeautifulSoup
import os
BASE_URL = "https://www.mse.mk"

def run_scraper():
    print("Updating stock data...")
    stocks = get_stocks()
    print(stocks)
    post_to_backend(stocks)
    print("Update done.")

def get_stock_name(detail_url):
    response = requests.get(detail_url)
    soup = BeautifulSoup(response.text, "html.parser")

    name_element = soup.select_one(".title")
    return name_element.text.strip() if name_element else "Unknown"

def get_stocks():
    url = f"{BASE_URL}/en"
    response = requests.get(url)
    soup = BeautifulSoup(response.text, "html.parser")

    current_prices = {}
    for li in soup.select(".newsticker a"):
        parts = li.text.strip().split(" ")
        if len(parts) >= 2:
            try:
                current_prices[parts[0]] = float(parts[1].replace(",", ""))
            except:
                continue

    stocks = []
    for row in soup.select(".tab-content-market-summary table tr")[1:]:
        cols = row.find_all("td")
        if len(cols) < 4:
            continue

        symbol_link = cols[0].find("a")
        if not symbol_link:
            continue

        symbol = symbol_link.text.strip()
        detail_url = BASE_URL + symbol_link.get("href")

        stocks.append({
            "symbol": symbol,
            "name": get_stock_name(detail_url),
            "lastPrice": float(cols[1].text.strip().replace(",", "")),
            "percentage": float(cols[2].text.strip()),
            "turnover": float(cols[3].text.strip().replace(",", "")),
            "currentPrice": current_prices.get(symbol)
        })

    return stocks

def post_to_backend(stocks):
    backend_url = os.getenv("BACKEND_URL", "http://backend-service.tradingmk.svc.cluster.local:8080")
    requests.post(f"{backend_url}/api/stocks/update", json=stocks)