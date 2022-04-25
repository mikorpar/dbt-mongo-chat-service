package com.mikorpar.brbljavac_api.services;

import com.mikorpar.brbljavac_api.data.dtos.users.UserWithoutPasswdDTO;
import com.mikorpar.brbljavac_api.data.models.ConfirmationToken;
import com.mikorpar.brbljavac_api.data.models.User;
import com.mikorpar.brbljavac_api.data.repositories.UserRepository;
import com.mikorpar.brbljavac_api.exceptions.tokens.NewerTokenFoundException;
import com.mikorpar.brbljavac_api.exceptions.tokens.TokenExpiredException;
import com.mikorpar.brbljavac_api.exceptions.tokens.TokenNotFoundException;
import com.mikorpar.brbljavac_api.exceptions.users.UserAlreadyActivatedException;
import com.mikorpar.brbljavac_api.exceptions.users.UserAlreadyExistsException;
import com.mikorpar.brbljavac_api.exceptions.users.UsernameTakenException;
import com.mikorpar.brbljavac_api.security.MyUserPrincipal;
import com.mikorpar.brbljavac_api.utils.LoggedUserFetcher;
import com.mikorpar.brbljavac_api.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final LoggedUserFetcher userFetcher;
    private final MailSenderService mailService;
    private final ConfirmationTokenService confTokenService;
    private final GroupService groupService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User does not exist in database");
        return new MyUserPrincipal(user, true, true, true, emptyList());
    }

    public User createUser(String email, String username, String password)
            throws UserAlreadyExistsException, UsernameTakenException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with email '%s' already exists", email));
        }
        if (userRepository.findByUsername(username) != null) {
            throw new UsernameTakenException(String.format("User with username '%s' already exists", username));
        }

        ConfirmationToken confToken = confTokenService.generateConfToken();
        User user = userRepository.save(new User(
                false,
                email,
                username,
                passwordEncoder.encode(password),
                Collections.singletonList(confToken)
        ));
        mailService.sendActivationMail(user.getEmail(), user.getUsername(), confToken.getToken());

        return user;
    }

    public void verifyRegistration(String token)
            throws TokenNotFoundException,
            UserAlreadyActivatedException,
            TokenExpiredException,
            NewerTokenFoundException
    {
        User user = userRepository.findByConfToken(token);
        if (user == null) throw new TokenNotFoundException(String.format("User with token '%s' not found", token));
        if (user.isActivated()) throw new UserAlreadyActivatedException("User already activated");

        ConfirmationToken confToken = user.getConfTokens().get(user.getConfTokens().size() - 1);
        if (!confToken.getToken().equals(token)) throw new NewerTokenFoundException("Newer token exists");

        try {
            confTokenService.confirmToken(confToken);
        } catch (TokenExpiredException ex) {
            sendNewConfToken(user);
            throw ex;
        }

        user.setActivated(true);
        userRepository.save(user);
    }

    private void sendNewConfToken(User user) {
        ConfirmationToken confToken = confTokenService.generateConfToken();
        user.getConfTokens().add(confToken);
        userRepository.save(user);
        mailService.sendActivationMail(user.getEmail(), user.getPassword(), confToken.getToken());
    }

    public void sendNewPassword(String email) {
        String password = passwordGenerator.generatePasswd();
        Optional<User> optUser = userRepository.findByEmail(email);

        optUser.ifPresent( user -> {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            mailService.sendPasswdRecoveryMail(user.getEmail(), user.getUsername(), password);
        });
    }

    public User updateUserCredentials(String username, String password) throws UsernameTakenException {
        if (userRepository.findByUsername(username) != null) {
            throw new UsernameTakenException(String.format("Username '%s' is taken", username));
        }

        User user = userFetcher.getPrincipal().getUser();
        if (username != null && !user.getUsername().equals(username)) user.setUsername(username);
        if (password != null) user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public void deleteCurrentUser() {
        User user = userFetcher.getPrincipal().getUser();
        groupService.removeUserFromAllGroups(user.getId());
        userRepository.delete(user);
    }

    public List<UserWithoutPasswdDTO> getUsers() {
        return userRepository.findAllByUsernameNot(userFetcher.getPrincipal().getUsername());
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
