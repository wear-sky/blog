package com.wearsky.demo.common.domain.header;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthoritiesHeader {

    private Long id;

    private List<String> authorities;
}
