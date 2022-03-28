package com.example.foundlost.services;

import com.thesis.findpet.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Data
@Service
public class UsersService {

    private UserRepository userRepository;

    private List<Integer> listNrUsers;

    @Scheduled(cron = "0/5 * * * * ?")
    public List<Integer> nrOfUsersList() {
        Integer nrUsers = userRepository.findAll().size();
        listNrUsers.add(nrUsers);
        listNrUsers.forEach(System.out::println);
        return listNrUsers;
    }



}
