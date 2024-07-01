package com.smarthome.smarthome.user;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="api/v1/users")
public class UserController
{
    private final UserService userService;

    @Autowired // Per dependency injection
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers()
    {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> registerNewUser(@RequestBody JSONObject jsonData)
    {
        User user = new User((String) jsonData.get("name"), (String) jsonData.get("email"));
        userService.addNewUser(user);
        return new ResponseEntity<>("Utente aggiunto", HttpStatus.OK);
    }

    @DeleteMapping(path = "{userId}")
    public void deleteUser(@PathVariable("userId") Long userId){
        userService.deleteUser(userId);
    }

    @PutMapping(path = "{userId}")
    public void updateUser(@PathVariable("userId") Long userId,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) String email)
    {
        userService.updateUser(userId, name, email);
    }
}
