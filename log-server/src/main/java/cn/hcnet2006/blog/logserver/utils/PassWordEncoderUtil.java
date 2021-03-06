package cn.hcnet2006.blog.logserver.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PassWordEncoderUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public static String BCryptPassword(String password){
        return encoder.encode(password);
    }
    public static boolean matches(CharSequence rawPassword, String encodedPassword){
        return encoder.matches(rawPassword,encodedPassword);
    }
}
