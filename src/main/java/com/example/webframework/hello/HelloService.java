package com.example.webframework.hello;

import com.example.webframework.user.Avatar;
import com.example.webframework.user.AvatarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HelloService {

    private final AvatarRepository avatarRepository;

    public String putAvatar() {
        avatarRepository.save(new Avatar("/avatar/avatar05.png", "#ffffff"));
        avatarRepository.save(new Avatar("/avatar/avatar06.png", "#ffffff"));
        avatarRepository.save(new Avatar("/avatar/avatar07.png", "#ffffff"));
        avatarRepository.save(new Avatar("/avatar/avatar08.png", "#ffffff"));


        return "success";
    }

}
