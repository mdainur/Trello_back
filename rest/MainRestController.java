package com.react.restapi.react_task_3.rest;

import com.react.restapi.react_task_3.dto.UserDTO;
import com.react.restapi.react_task_3.entities.CardTasks;
import com.react.restapi.react_task_3.entities.Cards;
import com.react.restapi.react_task_3.entities.Roles;
import com.react.restapi.react_task_3.entities.Users;
import com.react.restapi.react_task_3.repositories.RolesRepository;
import com.react.restapi.react_task_3.services.CardService;
import com.react.restapi.react_task_3.services.CardTasksService;
import com.react.restapi.react_task_3.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/api")
public class MainRestController {



    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private CardTasksService cardTasksService;

    @GetMapping(value = "/allCards")
    public ResponseEntity<?> getAllCards(){
        List<Cards> cards = cardService.getAllCards();
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @GetMapping(value = "/searchCards")
    public ResponseEntity<?> getSearchCards(@RequestParam(name = "name")String name){
        List<Cards> cards = cardService.searchCards(name);
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @GetMapping(value = "/allTasks")
    public ResponseEntity<?> getAllTasks(@RequestParam(name = "id")Long cardId){
        List<CardTasks> tasks = cardTasksService.getAllTasks(cardId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }



    @PostMapping(value = "/addCard")
    public ResponseEntity<?> addCard(@RequestBody Cards card){
        cardService.addCard(card);
        return ResponseEntity.ok(card);
    }

    @PostMapping(value = "/addTask")
    public ResponseEntity<?> addTask(@RequestBody CardTasks task){
        cardTasksService.addTask(task);
        return ResponseEntity.ok(task);
    }

    @GetMapping(value = "/getCard/{id}")
    public ResponseEntity<?> getCard(@PathVariable(name = "id") Long id){
        Cards card = cardService.getCard(id);
        if(card!=null){
            return ResponseEntity.ok(card);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/getTask/{id}")
    public ResponseEntity<?> getTask(@PathVariable(name = "id") Long id){
        CardTasks task = cardTasksService.getTask(id);
        if(task!=null){
            return ResponseEntity.ok(task);
        }else{
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping(value = "/saveCard")
    public ResponseEntity<?> saveCard(@RequestBody Cards card){
        cardService.editCard(card);
        return ResponseEntity.ok(card);
    }

    @PutMapping(value = "/saveTask")
    public ResponseEntity<?> saveTask(@RequestBody CardTasks task){
        cardTasksService.editTask(task);
        return ResponseEntity.ok(task);
    }


    @DeleteMapping(value = "/deleteCard")
    public ResponseEntity<?> deleteCard(@RequestBody Cards card){
        Cards checkCard = cardService.getCard(card.getId());
        if(checkCard!=null){
            cardService.deleteCard(checkCard);
            return ResponseEntity.ok(checkCard);
        }
        return ResponseEntity.ok(card);
    }


    @DeleteMapping(value = "/deleteTask")
    public ResponseEntity<?> deleteTask(@RequestBody CardTasks task){
        CardTasks checkTask = cardTasksService.getTask(task.getId());
        if(checkTask!=null){
            cardTasksService.deleteTask(checkTask);
            return ResponseEntity.ok(checkTask);
        }
        return ResponseEntity.ok(task);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registr(@RequestBody Users user) {
        System.out.println(user.getUsername());
        Users new_user =  userService.getUserByEmail(user.getUsername());
        if(new_user!=null){
            return ResponseEntity.notFound().build();
        }
        else{

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            List<Roles> roles = new ArrayList<>();
            roles.add(rolesRepository.getOne(3L));
            user.setRoles(roles);
            userService.saveUser(user);
            return ResponseEntity.ok().build();

        }
    }



    @GetMapping(value = "/profile")
    public ResponseEntity<?> profilePage(){
        Users user = getUser();
        return new ResponseEntity<>(new UserDTO(user.getId(), user.getEmail(),user.getFullName(), user.getRoles()), HttpStatus.OK);
    }

    private Users getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Users user = (Users) authentication.getPrincipal();
            return user;
        }
        return null;
    }


    @PutMapping(value = "/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody Users user){
        Users u =  userService.getUserByEmail(user.getUsername());
        if(u!=null){
            u.setFullName(user.getFullName());
            userService.saveUser(u);
            return ResponseEntity.ok(u);
        }
        return ResponseEntity.notFound().build();
    }




    @PutMapping(value = "/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody UserDTO user){
        Users u =  userService.getUserByEmail(user.getEmail());
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        if(!bCrypt.matches(user.getPassword(),u.getPassword())){
            return ResponseEntity.notFound().build();
        }
        else{
            u.setPassword(bCrypt.encode(user.getNewPassword()));
            userService.saveUser(u);
            return ResponseEntity.ok(u);
        }




    }


}