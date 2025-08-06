from bs4 import BeautifulSoup
import requests
from datetime import datetime, timedelta
import json

def get_stock_history(symbol, days=30):
    url = f"https://www.mse.mk/en/stats/symbolhistory/{symbol}"
    response = requests.get(url)
    soup = BeautifulSoup(response.text, "html.parser")

    history_data = []

    table = soup.select_one(".table")
    # dataTables_wrapper container-fluid dt-bootstrap4 no-footer ne e ova
    if not table:
        print("Table not found")
        return []

    rows = table.select("tbody tr")
    cutoff_date = datetime.now() - timedelta(days=days)

    for row in rows:
        cols = row.find_all("td")
        if len(cols) >= 5:
            try:
                date_str = cols[0].text.strip()
                date_obj = datetime.strptime(date_str, "%m/%d/%Y")

                if date_obj < cutoff_date:
                    break  # stop if date is older than cutoff

                close_price = float(cols[4].text.strip().replace(",", ""))
                history_data.append({
                    "symbol": symbol,
                    "timestamp": date_obj.strftime("%Y-%m-%d"),
                    "price": close_price
                })
            except Exception as e:
                print("Error parsing row:", e)

    return history_data



def post_history_to_backend(symbol):
    history_data = get_stock_history(symbol)
    print(json.dumps(history_data, indent=2))
    if history_data:
        url = "http://localhost:8080/api/history/upload"
        response = requests.post(url, json=history_data)
        print(f"Uploaded {len(history_data)} rows for {symbol}: {response.status_code}")

if __name__ == '__main__':
    post_history_to_backend("KMB")
    post_history_to_backend("ALK")
    post_history_to_backend("TEL")
    post_history_to_backend("REPL")
    post_history_to_backend("TNB")
    post_history_to_backend("PPIV")
    post_history_to_backend("UNI")
    post_history_to_backend("STB")
    post_history_to_backend("MPT")
    post_history_to_backend("TTK")
    post_history_to_backend("GRNT")
    post_history_to_backend("MTUR")