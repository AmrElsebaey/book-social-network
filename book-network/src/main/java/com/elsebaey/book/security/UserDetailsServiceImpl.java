package com.elsebaey.book.security;

//@Service
//@RequiredArgsConstructor
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    @Transactional
//    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
//        return userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//    }
//}
