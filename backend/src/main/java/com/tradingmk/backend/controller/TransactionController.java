package com.tradingmk.backend.controller;

import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.model.Transaction;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.repository.StockRepository;
import com.tradingmk.backend.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final StockRepository stockRepository;

    public TransactionController(TransactionRepository transactionRepository, StockRepository stockRepository) {
        this.transactionRepository = transactionRepository;
        this.stockRepository = stockRepository;
    }

    @GetMapping("/export")
    public void exportTransactions(HttpServletResponse response, @AuthenticationPrincipal User user) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.csv");

        List<Transaction> transactions = transactionRepository.findByUserId(user.getId());

        PrintWriter writer = response.getWriter();
        writer.println("ID,Stock,Type,Quantity,Price,Timestamp");

        for (Transaction t : transactions) {
            writer.println(
                    t.getId() + "," +
                            t.getStock().getSymbol() + "," +
                            t.getType() + "," +
                            t.getQuantity() + "," +
                            t.getPrice() + "," +
                            t.getTimestamp()
            );
        }

        writer.flush();
    }


    @PostMapping("/import")
    public ResponseEntity<String> importTransactions(@RequestParam("file") MultipartFile file,
                                                     @AuthenticationPrincipal User user) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            br.readLine(); //header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                Transaction transaction = new Transaction();
                transaction.setUser(user);

                Stock stock = stockRepository.findBySymbol(data[1])
                        .orElseThrow(() -> new RuntimeException("Stock not found: " + data[1]));
                transaction.setStock(stock);

                transaction.setType(data[2]);
                transaction.setQuantity(Integer.parseInt(data[3]));
                transaction.setPrice(Double.parseDouble(data[4]));
                transaction.setTimestamp(LocalDateTime.parse(data[5]));

                transactionRepository.save(transaction);
            }
        }

        return ResponseEntity.ok("Import successful!");
    }

    @GetMapping
    public List<Transaction> getUserTransactions(@AuthenticationPrincipal User user) {
        return transactionRepository.findByUserId(user.getId());
    }
}
