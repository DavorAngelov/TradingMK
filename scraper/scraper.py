from ctypes.wintypes import DOUBLE

import requests
from bs4 import BeautifulSoup
import time
BASE_URL = "https://www.mse.mk"
import json

def run_scraper_loop(interval=30):
    while True:
        print("Updating stock data...")
        stocks = get_stocks()
        print(stocks)
        post_to_backend(stocks)
        print("Update done. Waiting...")
        time.sleep(interval)

def get_stock_name(detail_url):
    response = requests.get(detail_url)
    soup = BeautifulSoup(response.text, "html.parser")


    name_element = soup.select_one(".title")
    if name_element:
        return name_element.text.strip()

    return "Unknown"



def get_stocks():
    url = f"{BASE_URL}/en"
    response = requests.get(url)
    soup = BeautifulSoup(response.text, "html.parser")


    current_prices = {}
    for li in soup.select(".newsticker a"):
        parts = li.text.strip().split(" ")
        if len(parts) >= 2:
            ticker = parts[0]
            price_str = parts[1].replace(",", "")
            try:
                current_price = float(price_str)
                current_prices[ticker] = current_price
            except ValueError:
                continue


    stocks = []
    for row in soup.select(".tab-content-market-summary table tr")[1:]:
        cols = row.find_all("td")
        if len(cols) >= 3:
            symbol_link = cols[0].find("a")
            if not symbol_link:
                continue

            symbol = symbol_link.text.strip()
            detail_path = symbol_link.get("href")
            detail_url = BASE_URL + detail_path

            name = get_stock_name(detail_url)
            last_price = float(cols[1].text.strip().replace(",", ""))
            percentage = float(cols[2].text.strip())
            turnover = float(cols[3].text.strip().replace(",", ""))

            current_price = current_prices.get(symbol, None)

            stock = {
                "symbol": symbol,
                "name": name if name != "Unknown" else symbol,
                "lastPrice": last_price,
                "percentage": percentage,
                "turnover": turnover,
                "currentPrice": current_price
            }
            stocks.append(stock)

    return stocks

def post_to_backend(stocks):
    url = "http://localhost:8080/api/stocks/update"
    res = requests.post(url, json=stocks)
    print(f"Posted {len(stocks)} stocks: {res.status_code}")


if __name__ == "__main__":
    run_scraper_loop(interval=30)

