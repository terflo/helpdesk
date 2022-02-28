package com.terflo.helpdesk;

import com.terflo.helpdesk.utils.RegexUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegexTest {

    @Autowired
    private RegexUtil regexUtil;

    @Test
    public void checkEmail() {

        Assertions.assertTrue(regexUtil.checkEmail("danil.krivoschiokov@yandex.ru"));

        Assertions.assertFalse(regexUtil.checkEmail("qwe"));

    }

    @Test
    public void checkUsername() {

        Assertions.assertTrue(regexUtil.checkUsername("terflo"));

        Assertions.assertTrue(regexUtil.checkUsername("root"));

        Assertions.assertFalse(regexUtil.checkUsername("qwe qwe 22"));

    }

}
