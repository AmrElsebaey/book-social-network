//package com.elsebaey.book.auth;
//
//import com.elsebaey.book.email.EmailService;
//import com.elsebaey.book.email.EmailTemplateName;
//import com.elsebaey.book.role.RoleRepository;
//import com.elsebaey.book.security.JwtService;
//import com.elsebaey.book.user.Token;
//import com.elsebaey.book.user.TokenRepository;
//import com.elsebaey.book.user.User;
//import com.elsebaey.book.user.UserRepository;
//import jakarta.mail.MessagingException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.security.SecureRandom;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//
////@Service
//@RequiredArgsConstructor
//public class AuthenticationService {
//
//    private final RoleRepository roleRepository;
//    private final PasswordEncoder PasswordEncoder;
//    private final UserRepository userRepository;
//    private final TokenRepository tokenRepository;
//    private final EmailService emailService;
//    private final AuthenticationManager authenticationManager;
//    private final JwtService jwtService;
//
//    @Value("${application.mailing.frontend.activation-url}")
//    private String activationUrl;
//
//
//    public void register(RegistrationRequest request) throws MessagingException {
//        var userRole = roleRepository.findByName("USER")
//                .orElseThrow(() -> new IllegalStateException("User role not found"));
//
//        var user = User.builder()
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .email(request.getEmail())
//                .password(PasswordEncoder.encode(request.getPassword()))
//                .accountLocked(false)
//                .enabled(false)
//                .roles(List.of(userRole))
//                .build();
//
//        userRepository.save(user);
//        sendValidationEmail(user);
//    }
//
//    private void sendValidationEmail(User user) throws MessagingException {
//        var newToken = generateAndSaveActivationToken(user);
//
//        emailService.sendEmail(
//                user.getEmail(),
//                user.fullName(),
//                EmailTemplateName.ACTIVATE_ACCOUNT,
//                activationUrl.concat("?token=" + newToken),
//                newToken,
//                "Account activation"
//        );
//
//    }
//
//    private String generateAndSaveActivationToken(User user) {
//        String generatedToken = generateActivationCode(6);
//        var token = Token.builder()
//                .token(generatedToken)
//                .createdAt(LocalDateTime.now())
//                .expiredAt(LocalDateTime.now().plusMinutes(15))
//                .user(user)
//                .build();
//        tokenRepository.save(token);
//        return generatedToken;
//    }
//
//    private String generateActivationCode(int length) {
//        String characters = "0123456789";
//        StringBuilder codeBuilder = new StringBuilder();
//        SecureRandom random = new SecureRandom();
//        for (int i = 0; i < length; i++) {
//            int index = random.nextInt(characters.length());
//            codeBuilder.append(characters.charAt(index));
//        }
//        return codeBuilder.toString();
//    }
//
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        var auth = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()));
//        var claims = new HashMap<String, Object>();
//        var user = ((User) auth.getPrincipal());
//        claims.put("fullName", user.fullName());
//        var token = jwtService.generateToken(claims, user);
//        return AuthenticationResponse.builder()
//                .token(token).build();
//    }
//
//    public void activateAccount(String token) throws MessagingException {
//        var storedToken = tokenRepository.findByToken(token)
//                .orElseThrow(() -> new IllegalStateException("Token not found"));
//        if (LocalDateTime.now().isAfter(storedToken.getExpiredAt())) {
//            sendValidationEmail(storedToken.getUser());
//            throw new IllegalStateException("Activation token is expired. A new one has been sent.");
//        }
//        var user = userRepository.findById(storedToken.getUser().getId())
//                .orElseThrow(() -> new IllegalStateException("User not found"));
//        user.setEnabled(true);
//        userRepository.save(user);
//        storedToken.setValidatedAt(LocalDateTime.now());
//        tokenRepository.save(storedToken);
//    }
//}
