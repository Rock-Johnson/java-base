package com.github.baseredis.cachebreakdown.dao;

import com.github.baseredis.cachebreakdown.model.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SysUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    @Select("select u.* from sys_user u   where u.username = #{username}")
    SysUser findUserByUsername(String username);

    @Select("select * from sys_user")
    List<SysUser> getUsers();
}