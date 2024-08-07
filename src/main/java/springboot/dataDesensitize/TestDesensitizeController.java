package springboot.dataDesensitize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestDesensitizeController {
    @GetMapping("/desensitize")
    public UserDTO getUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("孙大圣");
        userDTO.setEmail("shijun@163.com");
        userDTO.setPhoneNumber("12345678901");
        userDTO.setPassword("123456");
        userDTO.setAddress("辽宁省盘锦市兴隆台区红村乡441号");
        userDTO.setIdCard("447465200912089605");
        userDTO.setBankCard("6217000000000000000");
        userDTO.setGameName("超级无敌大铁锤");
        return userDTO;
    }
}
