package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    CardRepository cardRepository;

    @GetMapping("clients/current/cards")
    public List<CardDTO> getCards(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        return client.getCards().stream().map(card -> new CardDTO(card)).collect(toList());
    }

    @PostMapping("clients/current/cards")
    public ResponseEntity<Object> createCard (Authentication authentication, @RequestParam CardType cardType, @RequestParam CardColor cardColor) {
        Client client = clientRepository.findByEmail(authentication.getName());
        int cardLimit = 3;
        Set<Card> cards = client.getCards();

        if (cardType == CardType.CREDIT || cardType == CardType.DEBIT) {
            long cardsSameType = cards.stream()
                    .filter(newCard -> newCard.getType() == cardType)
                    .count();
            if (cardsSameType >= cardLimit) {
                return new ResponseEntity<>("You have requested the maximum number of cards of this type", HttpStatus.FORBIDDEN);
            }
        }
        // Creación de numero de terjeta
        String numberCard;
        do {
            Random random = new Random();
            numberCard = random.nextInt(9999)
                        + "-" + random.nextInt(9999)
                        + "-" + random.nextInt(9999)
                        + "-" + random.nextInt(9999);
        } while (cardRepository.findByNumber(numberCard) != null);

        // Creación de numero de cvv
        Random random = new Random();
        int cvvCard = random.nextInt(999);

        //creacion de la tarjeta
        Card card = new Card(client.getFirstName() + " " + client.getLastName(), cardType, cardColor, numberCard, cvvCard);
        client.addCard(card);
        cardRepository.save(card);
        return new ResponseEntity<>("Card created succesfully", HttpStatus.CREATED);

    }
}
