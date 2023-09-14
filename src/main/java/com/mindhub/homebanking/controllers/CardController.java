package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private CardService cardService;

    @GetMapping("clients/current/cards")
    public List<CardDTO> getCards(Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        return client.getCards().stream().map(card -> new CardDTO(card)).collect(toList());
    }

    @PostMapping("clients/current/cards")
    public ResponseEntity<Object> createCard (Authentication authentication, @RequestParam CardType cardType, @RequestParam CardColor cardColor) {
        Client client = clientService.findByEmail(authentication.getName());
        int cardLimit = 3;
        Set<Card> cards = client.getCards();
        if (cardType.toString().isBlank()) {
            return new ResponseEntity<>("Missing to complete the Card Typer ", HttpStatus.FORBIDDEN);
        }
        if (cardColor.toString().isBlank()) {
            return new ResponseEntity<>("Missing to complete the Card Color", HttpStatus.FORBIDDEN);
        }

            if (!cards.stream()
                                .filter(card -> card.getColor().equals(cardColor))
                                .filter(card -> card.getType()==cardType)
                                .collect(Collectors.toSet()).isEmpty()) {
                return new ResponseEntity<>("You have requested the maximum number of "+cardType+ " cards of this "+ cardColor, HttpStatus.FORBIDDEN);
            }


        // Creación de numero de tarjeta usando CardUtils
        String numberCard  = CardUtils.getNumberCard();
        while (cardService.findByNumber(numberCard) != null);

        // Creación de numero de cvv usando CardUtils
        int cvvCard = CardUtils.getCvv();

        //creacion de la tarjeta
        Card card = new Card(client.getFirstName() + " " + client.getLastName(), cardType, cardColor, numberCard, cvvCard);
        client.addCard(card);
        cardService.saveCard(card);
        return new ResponseEntity<>("Card created succesfully", HttpStatus.CREATED);

    }
}
