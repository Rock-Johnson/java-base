package com.github.baseredis.cachebreakdown.service.impl;



import com.github.baseredis.cachebreakdown.dao.SysUserDao;
import com.github.baseredis.cachebreakdown.model.SysUser;
import com.github.baseredis.cachebreakdown.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName SysUserServiceImpl
 * @Description TODO
 * @Author zhy
 * @Date 2018/12/10 22:15
 * @Version 1.0
 **/
@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {


    @Autowired
    private SysUserDao sysUserDao;


    @Override
    public List<SysUser> getUser() {
        return sysUserDao.getUsers();
    }
}
