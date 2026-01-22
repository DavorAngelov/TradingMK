INSERT INTO users (id, email, password, role, username) VALUES
(1, 'davor@example.com', 'hashed_password_1', 'USER', 'davor'),
(2, 'ana@example.com', 'hashed_password_2', 'USER', 'ana'),
(3, 'marko@example.com', 'hashed_password_3', 'ADMIN', 'marko');

INSERT INTO stock (id, symbol, name, current_price, last_price, percentage, turnover, last_updated) VALUES
(1, 'ALK', 'Alkaloid AD Skopje', 1500.00, 1490.00, 0.67, 120000.00, '2026-01-21 10:00:00'),
(2, 'KMB', 'Komercijalna banka AD Skopje', 950.50, 940.00, 1.11, 85000.00, '2026-01-21 10:00:00'),
(3, 'MPT', 'Makpetrol AD Skopje', 320.75, 315.00, 1.84, 50000.00, '2026-01-21 10:00:00'),
(4, 'STBP', 'Stopanska banka AD Skopje', 420.25, 410.00, 2.52, 60000.00, '2026-01-21 10:00:00');

INSERT INTO portfolios (id, balance, user_id) VALUES
(1, 10000.00, 1),
(2, 5000.00, 2),
(3, 7000.00, 3);


INSERT INTO portfolio_holdings (id, quantity, avg_price, stock_symbol, portfolio_id) VALUES
(1, 10, 1480.00, 'ALK', 1),
(2, 5, 940.00, 'KMB', 1),
(3, 20, 315.00, 'MPT', 2),
(4, 15, 410.00, 'STBP', 3);

INSERT INTO stock_history (id, price, symbol, timestamp) VALUES
(1, 1490.00, 'ALK', '2026-01-20'),
(2, 1500.00, 'ALK', '2026-01-21'),
(3, 940.00, 'KMB', '2026-01-20'),
(4, 950.50, 'KMB', '2026-01-21'),
(5, 315.00, 'MPT', '2026-01-20'),
(6, 320.75, 'MPT', '2026-01-21'),
(7, 410.00, 'STBP', '2026-01-20'),
(8, 420.25, 'STBP', '2026-01-21');

INSERT INTO user_auth_providers (user_id, auth_providers) VALUES
(1, 'INTERNAL'),
(2, 'GOOGLE'),
(3, 'INTERNAL');

INSERT INTO watchlist (id, price_above, price_below, stock_id, user_id) VALUES
(1, 1550.00, 1450.00, 1, 1),   -- Davor sledi ALK
(2, 1000.00, 900.00, 2, 1),    -- Davor sledi KMB
(3, 330.00, 310.00, 3, 2),     -- Ana sledi MPT
(4, 430.00, 400.00, 4, 3);     -- Marko sledi STBP
