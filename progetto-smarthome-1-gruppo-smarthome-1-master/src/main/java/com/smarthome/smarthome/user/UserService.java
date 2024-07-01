package com.smarthome.smarthome.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService
{
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public void addNewUser(User user)
    {
        Optional<User> userOptional = userRepository.findUserByName(user.getName());

        if (userOptional.isPresent())
            throw new IllegalStateException("Impossibile aggiungere utente. Utente già presente.");

        userRepository.save(user);
    }

    public void deleteUser(Long userId)
    {
        boolean exists = userRepository.existsById(userId);

        if (!exists)
            throw new IllegalStateException("Impossibile eliminare l'utente. L'utente non esiste.");

        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUser(Long userId, String name, String email)
    {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Impossibile aggiornare l'utente. L'utente non esiste"));

        if (name != null && name.length() > 0 && !Objects.equals(u.getName(), name))
            u.setName(name);

        if (email != null && email.length() > 0 && !Objects.equals(u.getEmail(), email))
            u.setEmail(email);
    }
}