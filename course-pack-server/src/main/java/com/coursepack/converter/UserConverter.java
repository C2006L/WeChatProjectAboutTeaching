package com.coursepack.converter;

import com.coursepack.dto.auth.UserVO;
import com.coursepack.entity.User;
import org.mapstruct.Mapper;

/**
 * User 实体 → UserVO（MapStruct）
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    UserVO toVO(User user);
}