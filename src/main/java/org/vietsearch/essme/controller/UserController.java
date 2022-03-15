package org.vietsearch.essme.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.filter.AuthenticatedRequest;
import org.vietsearch.essme.model.event.Event;
import org.vietsearch.essme.model.user.User;
import org.vietsearch.essme.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    final
    UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        User user = userRepository.findByUid(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    @GetMapping
    public List<User> getUsers(@RequestParam(required = false) Integer limit) {
        if (limit == null) {
            return userRepository.findAll();
        }
        return userRepository.findAll(PageRequest.of(0, limit)).getContent();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User update(AuthenticatedRequest request,@PathVariable String id, @RequestBody User user) {
        String uuid = request.getUserId();
        userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        user.setUid(id);
        if (!matchUser(uuid, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
        }
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(AuthenticatedRequest request,@PathVariable String id) {
        String uuid = request.getUserId();
        if (!matchUser(uuid, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied", null);
        }
        userRepository.deleteById(id);
    }

    private boolean matchUser(String uuid, String userChangedId) {
        return uuid.equals(userChangedId);
    }
}
