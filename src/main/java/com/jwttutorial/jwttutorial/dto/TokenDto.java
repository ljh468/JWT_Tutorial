package com.jwttutorial.jwttutorial.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * Token정보를 Response할때 사용할 TokenDTO
 */
public class TokenDto {
    private String token;
}
