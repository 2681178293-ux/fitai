package net.wanho.fitai.modules.member.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import net.wanho.fitai.modules.member.model.dto.UsrSettingAddDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSettingQueryDTO;
import net.wanho.fitai.modules.member.model.dto.UsrSettingUpdateDTO;
import net.wanho.fitai.modules.member.model.po.UsrSetting;
import net.wanho.fitai.modules.member.model.vo.UsrSettingVO;

public interface UsrSettingServiceI extends MPJBaseService<UsrSetting> {
    IPage<UsrSettingVO> findByPage(UsrSettingQueryDTO queryDto);

    void add(UsrSettingAddDTO addDto);

    void update(UsrSettingUpdateDTO updateDto);

    void delete(String id);

    UsrSettingVO findById(Long id);

}
