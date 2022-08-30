package antifraud.Controllers;

import antifraud.DB.Entities.CardLimit;
import antifraud.DB.Entities.Transaction;
import antifraud.DB.Repositories.CardLimitRepository;
import antifraud.DB.Repositories.StolenCardRepository;
import antifraud.DB.Repositories.SuspiciousIpRepository;
import antifraud.DB.Repositories.TransactionRepository;
import antifraud.Enums.Regions;
import antifraud.Enums.TransStatus;
import antifraud.Exception.TransactionException;
import antifraud.RequestAndResponse.TransactionFeedback;
import antifraud.RequestAndResponse.TransactionResponse;
import antifraud.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToIntBiFunction;

@RestController
public class TransactionController {

    @Autowired
    private TransactionRepository transRepository;

    @Autowired
    private SuspiciousIpRepository ipRepository;

    @Autowired
    private StolenCardRepository cardRepository;

    @Autowired
    private CardLimitRepository cardLimitRepository;

    @PostMapping(value = "/api/antifraud/transaction")
    public ResponseEntity postTransaction (@RequestBody Transaction trans) {
        try {
            if(Valid.checkIp(trans.getIp()) && Valid.checkCard(trans.getNumber()) && Regions.checkRegion(trans.getRegion())){
                if(trans.getAmount() > 0) {

                    TransactionResponse response = new TransactionResponse();

                    if(!cardLimitRepository.existsById(trans.getNumber())){
                        cardLimitRepository.save(new CardLimit().setCardNumber(trans.getNumber()));
                    }
                    System.out.println(cardLimitRepository.findByNumber(trans.getNumber()).get().getAllowLimit());
                    System.out.println(cardLimitRepository.findByNumber(trans.getNumber()).get().getManualLimit());
                    System.out.println(trans.getAmount());
                    if(trans.getAmount() <=  cardLimitRepository.findByNumber(trans.getNumber()).get().getAllowLimit()) {
                        response.setTransResp(TransStatus.ALLOWED, "none");
                    } else if(trans.getAmount() <=  cardLimitRepository.findByNumber(trans.getNumber()).get().getManualLimit()) {
                        response.setTransResp(TransStatus.MANUAL_PROCESSING, "amount");
                    } else {
                        response.setTransResp(TransStatus.PROHIBITED, "amount");
                    }


                    if(cardRepository.existsByNumber(trans.getNumber())) {
                        response.setTransResp(TransStatus.PROHIBITED, "card-number");
                    }

                    if(ipRepository.existsByIp(trans.getIp())) {
                        response.setTransResp(TransStatus.PROHIBITED, "ip");
                    }

                    if(transRepository.countIps(trans) == 2) {
                        response.setTransResp(TransStatus.MANUAL_PROCESSING, "ip-correlation");
                    } else if(transRepository.countIps(trans) > 2) {
                        response.setTransResp(TransStatus.PROHIBITED, "ip-correlation");
                    }

                    if(transRepository.countRegions(trans) == 2) {
                        response.setTransResp(TransStatus.MANUAL_PROCESSING, "region-correlation");
                    } else if(transRepository.countRegions(trans) > 2) {
                        response.setTransResp(TransStatus.PROHIBITED, "region-correlation");
                    }

                    trans.setResult(response.getResult().getTransStatus());
                    transRepository.save(trans);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
                throw new TransactionException("Incorrect amount of transaction. It must be greater than zero.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.out.println("EEE" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @GetMapping("/api/antifraud/history")
    public ResponseEntity getHistoryTransactions() {
        try {
            List<Transaction> transHistory = transRepository.findAllByOrderByTransactionIdAsc();
            return ResponseEntity.status(HttpStatus.OK).body(transHistory);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/api/antifraud/history/{number}")
    public ResponseEntity getHistoryTransactionsByNumber (@PathVariable String number) {
        try {
            if(Valid.checkCard(number)){
               if(transRepository.existsByNumber(number)) {
                   List<Transaction> transHistoryByNumber = transRepository.findAllByNumberOrderByTransactionIdAsc(number);
                   return ResponseEntity.status(HttpStatus.OK).body(transHistoryByNumber);
               }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/api/antifraud/transaction")
    public ResponseEntity putTransaction(@RequestBody TransactionFeedback transFeedback) {
        try {
            if(transRepository.existsById(transFeedback.getTransactionId())){
                Transaction trans = transRepository.findById(transFeedback.getTransactionId()).get();
                if(trans.getFeedback().equals("")) {
                    if(!trans.getResult().equals(transFeedback.getFeedback().getTransStatus())){
                        trans.setFeedback(transFeedback.getFeedback().getTransStatus());
                        limitChanger(trans);
                        transRepository.save(trans);
                        return ResponseEntity.status(HttpStatus.OK).body(trans);
                    }
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private void limitChanger (Transaction trans) throws Exception{
        CardLimit cardLimit = cardLimitRepository.findById(trans.getNumber()).get();

        switch (trans.getFeedback()) {
            case "ALLOWED" :
                cardLimit.setAllowLimit(upLimit.applyAsInt(cardLimit.getAllowLimit(),trans.getAmount()));
                if(trans.getResult().equals(TransStatus.PROHIBITED.getTransStatus())){
                    cardLimit.setManualLimit(upLimit.applyAsInt(cardLimit.getManualLimit(),trans.getAmount()));
                }
                break;
            case "MANUAL_PROCESSING" :
                if(trans.getResult().equals(TransStatus.ALLOWED.getTransStatus())) {
                    cardLimit.setAllowLimit(downLimit.applyAsInt(cardLimit.getAllowLimit(),trans.getAmount()));
                } else if(trans.getResult().equals(TransStatus.PROHIBITED.getTransStatus())){
                    cardLimit.setManualLimit(upLimit.applyAsInt(cardLimit.getManualLimit(),trans.getAmount()));
                }
                break;
            case "PROHIBITED" :
                if(trans.getResult().equals(TransStatus.ALLOWED.getTransStatus())) {
                    cardLimit.setAllowLimit(downLimit.applyAsInt(cardLimit.getAllowLimit(),trans.getAmount()));
                }
                cardLimit.setManualLimit(downLimit.applyAsInt(cardLimit.getManualLimit(),trans.getAmount()));
                break;
        }

        cardLimitRepository.save(cardLimit);
    }

    private ToIntBiFunction<Integer,Long> upLimit = (current_limit,value_from_transaction) -> (int) Math.ceil(0.8 * current_limit + 0.2 * value_from_transaction);
    private ToIntBiFunction<Integer,Long> downLimit = (current_limit,value_from_transaction) -> (int) Math.ceil(0.8 * current_limit - 0.2 * value_from_transaction);

}
