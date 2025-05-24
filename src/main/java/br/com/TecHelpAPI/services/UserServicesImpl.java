package br.com.TecHelpAPI.services;

import br.com.TecHelpAPI.data.dto.UserDTO;
import br.com.TecHelpAPI.exception.ResourceNotFoundException;
import static br.com.TecHelpAPI.mapper.ObjectMapper.parseListObjects;
import static br.com.TecHelpAPI.mapper.ObjectMapper.parseObject;
import br.com.TecHelpAPI.model.User;
import br.com.TecHelpAPI.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServicesImpl extends UserServices {

    private final Logger logger = LoggerFactory.getLogger(UserServicesImpl.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository repository;

    @Override
    public List<UserDTO> findAll() {
        logger.info("Finding all users!");
        return parseListObjects(repository.findAll(), UserDTO.class);
    }

    @Override
    public UserDTO findById(Long id) {
        logger.info("Finding user by ID: {}", id);
        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return parseObject(entity, UserDTO.class);
    }

    @Override
    public UserDTO create(UserDTO userDTO) {
        logger.info("Creating new user");
        if (repository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ResourceNotFoundException("Email already exists");
        }

        User user = parseObject(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return parseObject(repository.save(user), UserDTO.class);
    }

    @Override
    public UserDTO update(UserDTO userDTO) {
        logger.info("Updating user with ID: {}", userDTO.getIdUser());
        User existingUser = repository.findById(userDTO.getIdUser())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existingUser.setNameUser(userDTO.getNameUser());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setDept(userDTO.getDept());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        return parseObject(repository.save(existingUser), UserDTO.class);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting user with ID: {}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        repository.delete(user);
    }

    @Override
    public boolean authenticate(String nameUser, String password) {
        logger.info("Authenticating user: {}", nameUser);
        return repository.findByNameUser(nameUser)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }
}