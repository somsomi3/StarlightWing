package com.minjoo.StarlightWing.service;

import java.util.List;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;


@Service
@ComponentScan
public interface TokenBlackListService {

    void addTokenToList(String value);

    boolean isContainToken(String value);

    List<Object> getTokenBlackList();

    void removeToken(String value);
}
