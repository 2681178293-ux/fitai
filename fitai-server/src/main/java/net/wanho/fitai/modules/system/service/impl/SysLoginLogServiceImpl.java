package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import io.github.linpeilie.Converter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.util.AddressUtil;
import net.wanho.fitai.infra.util.ServletUtil;
import net.wanho.fitai.modules.system.async.LoginLogAsyncWriter;
import net.wanho.fitai.modules.system.mapper.SysLoginLogMapper;
import net.wanho.fitai.modules.system.model.dto.SysLoginLogQueryDTO;
import net.wanho.fitai.modules.system.model.po.SysLoginLog;
import net.wanho.fitai.modules.system.model.vo.SysLoginLogVO;
import net.wanho.fitai.modules.system.service.SysLoginLogServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl extends MPJBaseServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogServiceI {

    private final Converter converter;
    private final LoginLogAsyncWriter loginLogAsyncWriter;

    @Override
    public IPage<SysLoginLogVO> findByPage(SysLoginLogQueryDTO queryDto) {
        IPage<SysLoginLog> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getUserName()), SysLoginLog::getUserName, queryDto.getUserName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getUserType()), SysLoginLog::getUserType, queryDto.getUserType());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysLoginLog::getStatus, queryDto.getStatus());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getClientType()), SysLoginLog::getClientType, queryDto.getClientType());
        IPage<SysLoginLog> result = this.page(page, wrapper);
        return result.convert(item -> converter.convert(item, SysLoginLogVO.class));
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysLoginLogVO findById(Long id) {
        SysLoginLog entity = this.getById(id);
        return converter.convert(entity, SysLoginLogVO.class);
    }

    @Override
    public void recordLoginLog(String userName,String userType,String clientType, String status, String msg) {

        HttpServletRequest request = ServletUtil.getRequest();
        String clientIP = AddressUtil.getClientIP(request);
        String ipLocation = AddressUtil.getIpLocation(clientIP);

        String userAgentStr = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgentUtil.parse(userAgentStr);

        SysLoginLog sysLoginLog = new SysLoginLog();
        sysLoginLog.setUserName(userName);
        sysLoginLog.setUserType(userType);
        sysLoginLog.setClientType(clientType);
        sysLoginLog.setLoginIp(clientIP);
        sysLoginLog.setLoginLocation(ipLocation);
        sysLoginLog.setBrowser(userAgent.getBrowser().getName());
        sysLoginLog.setOs(userAgent.getOs().getName());
        sysLoginLog.setStatus(status);
        sysLoginLog.setMsg(msg);

        //this.save(sysLoginLog);
        loginLogAsyncWriter.write(sysLoginLog);


    }
}
