package au.edu.uowmail.law880.controllers;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import au.edu.uowmail.law880.repositories.UserRepository;
import au.edu.uowmail.law880.models.User;

@RestController(value="users")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping(value="/new")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> body) {
        if(body.containsKey("firstName") && body.containsKey("lastName") && body.containsKey("email") && body.containsKey("birthDate")) {
            String fName = body.get("firstName");
            String lName = body.get("lastName");
            String email = body.get("email");
            String birth = body.get("birthDate");
            Date birthDate;
            try {
                 birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(birth);
            } catch(ParseException e) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "Incorrect birth date format");
                return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
            }

            if(!email.contains("@")) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "Invalid email format");
                return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
            }

            if(userRepo.existsByEmail(email)) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "A user with the same email address already exists");
                return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
            }

            User newUser = new User(fName, lName, email, birthDate);

            newUser = userRepo.save(newUser);
            if(newUser.getId() != null) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
            else {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "Unknown error occurred");
                return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("message", "Request is missing required fields");
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value="/{email}/delete")
    public HttpStatus deleteUser(@PathVariable("email") final String email) {
        Optional<User> userQuery = userRepo.findByEmail(email);
        if(userQuery.isPresent()) {
            User userToDelete = userQuery.get();
            userRepo.delete(userToDelete);
            return HttpStatus.OK;
        }
        else {
            return HttpStatus.NOT_FOUND;
        }
    }

    @GetMapping(value="/{email}")
    public ResponseEntity<?> getUser(@PathVariable("email") final String email) {
        Optional<User> userQuery = userRepo.findByEmail(email);
        if(userQuery.isPresent())
            return new ResponseEntity<>(userQuery.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value="/all")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

}
