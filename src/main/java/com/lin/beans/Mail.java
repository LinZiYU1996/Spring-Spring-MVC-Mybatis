package com.lin.beans;

import lombok.*;

import java.util.Set;

/**
 * Created by linziyu on 2019/1/23.
 * 封装mail的信息
 */

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mail {

    private String subject;

    private String message;

    private Set<String> receivers;
}
