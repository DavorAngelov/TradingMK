from datetime import time

from scraper import run_scraper
from scraper_historical_data import post_history_for_all

if __name__ == "__main__":
    while True:
        try:
            print("Starting scraper...")

            run_scraper()
            post_history_for_all()

            print("Scraper finished. Sleeping for 200 seconds...")

        except Exception as e:
            print(f"Scraper failed: {e}")

        time.sleep(200)