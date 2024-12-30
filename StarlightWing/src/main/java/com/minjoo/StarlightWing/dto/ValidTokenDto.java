package com.minjoo.StarlightWing.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ValidTokenDto {

    private boolean isValid = false;
    private String errorName = "";

    @Builder(toBuilder = true)
    public ValidTokenDto(boolean isValid, String errorName) {
        this.isValid = isValid;
        this.errorName = errorName;
    }
}
