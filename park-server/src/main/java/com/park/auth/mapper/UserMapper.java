package com.park.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.park.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper
 *
 * @author park-team
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}
