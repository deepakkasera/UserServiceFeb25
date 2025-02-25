package com.example.userservicefeb25.services;

import com.example.userservicefeb25.dtos.SendEmailDto;
import com.example.userservicefeb25.models.Token;
import com.example.userservicefeb25.models.User;
import com.example.userservicefeb25.repositories.TokenRepository;
import com.example.userservicefeb25.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private KafkaTemplate<String, String> kafkaTemplate; //Key -> Topic, Value -> Event
    private ObjectMapper objectMapper;

    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder,
                           UserRepository userRepository,
                           TokenRepository tokenRepository,
                           KafkaTemplate kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Token login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            //Throw an Exception OR Redirect to signup
            return null;
        }

        //Match the password.
        User user = optionalUser.get();

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            //Password mismatch
            return null;
        }

        //Login success -> Generate Token
        Token token = new Token();
        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        token.setUser(user);

        LocalDate localDate = LocalDate.now().plusDays(30);
        Date expiryDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        token.setExpiryAt(expiryDate);

        return tokenRepository.save(token);
    }

    @Override
    public User signUp(String name, String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        //Push an event to Kafka which UserService will read and send a Welcome email to the user.
        SendEmailDto emailDto = new SendEmailDto();
        emailDto.setSubject("Welcome to Scaler!!");
        emailDto.setBody("Happy to have you onboard.");
        emailDto.setEmail(email);

        //Push an event to Kafka
        try {
            kafkaTemplate.send(
                    "sendEmail",
                    objectMapper.writeValueAsString(emailDto)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return userRepository.save(user);
    }

    @Override
    public User validateToken(String tokenValue) {
        //Token value should be present in the DB, deleted should be false
        // and expiry time > current time

        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(
                tokenValue,
                false,
                new Date()
        );

        if (optionalToken.isEmpty()) {
            return null;
        }

        return optionalToken.get().getUser();
    }

    @Override
    public void logout(String tokenValue) {

    }
}
