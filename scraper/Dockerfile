
FROM python:3.12-slim


RUN apt-get update && apt-get install -y --no-install-recommends \
        gcc \
        libpq-dev \
    && rm -rf /var/lib/apt/lists/*


RUN adduser --disabled-password --gecos "" scraper
WORKDIR /app


COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .
RUN chown -R scraper:scraper /app
USER scraper


CMD ["python", "main.py"]

