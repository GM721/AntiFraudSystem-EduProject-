package antifraud.Controllers;

import antifraud.DB.Entities.StolenCard;
import antifraud.DB.Entities.SuspiciousIp;
import antifraud.DB.Repositories.StolenCardRepository;
import antifraud.DB.Repositories.SuspiciousIpRepository;
import antifraud.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class SupportController {
    @Autowired
    private SuspiciousIpRepository ipRepository;

    @Autowired
    private StolenCardRepository cardRepository;

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity postSusIp (@RequestBody SuspiciousIp ip) {
        try {
            if (Valid.checkIp(ip.getIp())){
                if(!ipRepository.existsByIp(ip.getIp())){
                    ipRepository.save(ip);
                    return ResponseEntity.status(HttpStatus.OK).body(ipRepository.findSuspiciousIpByIp(ip.getIp()).get());
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity postStolenCard(@RequestBody StolenCard stolenCard) {
        try {
            if (Valid.checkCard(stolenCard.getNumber())){
                if(!cardRepository.existsByNumber(stolenCard.getNumber())){
                    cardRepository.save(stolenCard);
                    return ResponseEntity.status(HttpStatus.OK).body(cardRepository.findStolenCardByNumber(stolenCard.getNumber()).get());
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity getSusIp() {
        try {
            List<SuspiciousIp> ipList = ipRepository.findAllByOrderByIdAsc();
            return ResponseEntity.status(HttpStatus.OK).body(ipList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/api/antifraud/stolencard")
    public ResponseEntity getStolenCard() {
        try {
            List<StolenCard> cardList = cardRepository.findAllByOrderByIdAsc();
            return ResponseEntity.status(HttpStatus.OK).body(cardList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity deleteSusIp (@PathVariable String ip) {
        try {
            if(Valid.checkIp(ip)){
                if(ipRepository.existsByIp(ip)){
                    ipRepository.removeByIp(ip);
                    return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", String.format("IP %s successfully removed!", ip)));
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public ResponseEntity deleteStolenCard (@PathVariable String number) {
        try {
            if(Valid.checkCard(number)) {
                if(cardRepository.existsByNumber(number)) {
                    cardRepository.deleteByNumber(number);
                    return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", String.format("Card %s successfully removed!", number)));
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
