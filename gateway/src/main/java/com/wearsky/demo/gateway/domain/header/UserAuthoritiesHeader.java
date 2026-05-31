package com.wearsky.demo.gateway.domain.header;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class UserAuthoritiesHeader implements Serializable {

    private Long id;

    private List<String> authorities;
}
