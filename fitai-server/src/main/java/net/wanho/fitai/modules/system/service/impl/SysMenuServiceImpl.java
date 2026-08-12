package net.wanho.fitai.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.linpeilie.Converter;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.auth.constant.CommonConstant;
import net.wanho.fitai.infra.auth.helper.AdminLoginHelper;
import net.wanho.fitai.modules.system.mapper.SysMenuMapper;
import net.wanho.fitai.modules.system.model.dto.SysMenuAddDTO;
import net.wanho.fitai.modules.system.model.dto.SysMenuQueryDTO;
import net.wanho.fitai.modules.system.model.dto.SysMenuUpdateDTO;
import net.wanho.fitai.modules.system.model.po.SysMenu;
import net.wanho.fitai.modules.system.model.po.SysRoleMenu;
import net.wanho.fitai.modules.system.model.po.SysUserRole;
import net.wanho.fitai.modules.system.model.vo.RouterVO;
import net.wanho.fitai.modules.system.model.vo.SysMenuVO;
import net.wanho.fitai.modules.system.service.SysMenuServiceI;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends MPJBaseServiceImpl<SysMenuMapper, SysMenu> implements SysMenuServiceI {

    private static final String MENU_TYPE_DIR = "M";
    private static final String MENU_TYPE_BUTTON = "F";
    /** 与库约定一致：0 显示 */
    private static final String VISIBLE_SHOW = "0";

    /**
     * 库里 path 与前端 views 路径不一致时的兜底映射（component 列为空时使用）。
     */
    private static final Map<String, String> PATH_COMPONENT_MAP = new LinkedHashMap<>();

    static {
        PATH_COMPONENT_MAP.put("/dashboard", "/home/index");
        PATH_COMPONENT_MAP.put("/user/customer-list", "/user/list/index");
        PATH_COMPONENT_MAP.put("/user/body-data", "/user/body/index");
        PATH_COMPONENT_MAP.put("/user/coach", "/coach/list/index");
        PATH_COMPONENT_MAP.put("/user/segment", "/user/segment/index");
        PATH_COMPONENT_MAP.put("/user/booking", "/coach/booking/index");
        PATH_COMPONENT_MAP.put("/member/package", "/member/plan/index");
        PATH_COMPONENT_MAP.put("/member/level", "/member/level/index");
        PATH_COMPONENT_MAP.put("/member/benefit", "/member/benefit/index");
        PATH_COMPONENT_MAP.put("/member/points", "/member/points-rule/index");
        PATH_COMPONENT_MAP.put("/course/category", "/course/category/index");
        PATH_COMPONENT_MAP.put("/course/list", "/course/list/index");
        PATH_COMPONENT_MAP.put("/course/video", "/course/video/index");
        PATH_COMPONENT_MAP.put("/community/feed", "/community/post/index");
        PATH_COMPONENT_MAP.put("/community/moderation", "/community/moderation/index");
        PATH_COMPONENT_MAP.put("/train/action-lib", "/training/exercise/index");
        PATH_COMPONENT_MAP.put("/train/plan", "/training/plan/index");
        PATH_COMPONENT_MAP.put("/train/record", "/training/record/index");
        PATH_COMPONENT_MAP.put("/train/device", "/training/device/index");
        PATH_COMPONENT_MAP.put("/diet/category", "/diet/food-category/index");
        PATH_COMPONENT_MAP.put("/diet/food", "/diet/food/index");
        PATH_COMPONENT_MAP.put("/diet/plan", "/diet/meal-plan/index");
        PATH_COMPONENT_MAP.put("/diet/record", "/diet/record/index");
        PATH_COMPONENT_MAP.put("/ai/stats", "/ai/stats/index");
        PATH_COMPONENT_MAP.put("/ai/knowledge", "/ai/knowledge/index");
        PATH_COMPONENT_MAP.put("/ai/chat", "/ai/conversation/index");
        PATH_COMPONENT_MAP.put("/ai/model", "/ai/model/index");
        PATH_COMPONENT_MAP.put("/ai/prompt", "/ai/prompt/index");
        PATH_COMPONENT_MAP.put("/ai/rag", "/ai/rag-evaluate/index");
        PATH_COMPONENT_MAP.put("/ai/plan-review", "/ai/plan-review/index");
        PATH_COMPONENT_MAP.put("/ai/agent", "/ai/agent/index");
        PATH_COMPONENT_MAP.put("/ai/tools", "/ai/tool/index");
        PATH_COMPONENT_MAP.put("/ai/dataset", "/ai/dataset/index");
        PATH_COMPONENT_MAP.put("/ai/evaluation", "/ai/evaluation/index");
        PATH_COMPONENT_MAP.put("/ai/guardrail", "/ai/guardrail/index");
        PATH_COMPONENT_MAP.put("/ai/vector", "/ai/vector-index/index");
        PATH_COMPONENT_MAP.put("/ai/trace", "/ai/trace/index");
        PATH_COMPONENT_MAP.put("/ai/cost", "/ai/cost/index");
        PATH_COMPONENT_MAP.put("/ai/quota", "/ai/quota/index");
        PATH_COMPONENT_MAP.put("/ai/token", "/ai/token-usage/index");
        PATH_COMPONENT_MAP.put("/product/category", "/product/category/index");
        PATH_COMPONENT_MAP.put("/product/brand", "/product/brand/index");
        PATH_COMPONENT_MAP.put("/product/list", "/product/list/index");
        PATH_COMPONENT_MAP.put("/product/stock", "/product/stock/index");
        PATH_COMPONENT_MAP.put("/product/attr", "/product/attr/index");
        PATH_COMPONENT_MAP.put("/order/list", "/order/list/index");
        PATH_COMPONENT_MAP.put("/order/member", "/member/order/index");
        PATH_COMPONENT_MAP.put("/order/refund", "/order/refund/index");
        PATH_COMPONENT_MAP.put("/order/payment", "/order/payment-channel/index");
        PATH_COMPONENT_MAP.put("/order/logistics", "/order/logistics/index");
        PATH_COMPONENT_MAP.put("/marketing/banner", "/marketing/banner/index");
        PATH_COMPONENT_MAP.put("/marketing/coupon", "/marketing/coupon/index");
        PATH_COMPONENT_MAP.put("/marketing/flash", "/seckill/list/index");
        PATH_COMPONENT_MAP.put("/marketing/challenge", "/marketing/challenge/index");
        PATH_COMPONENT_MAP.put("/system/account", "/system/account/index");
        PATH_COMPONENT_MAP.put("/system/role", "/system/role/index");
        PATH_COMPONENT_MAP.put("/system/menu", "/system/menu/index");
        PATH_COMPONENT_MAP.put("/system/dept", "/system/department/index");
        PATH_COMPONENT_MAP.put("/system/dict", "/system/dict/index");
        PATH_COMPONENT_MAP.put("/system/job", "/system/task/index");
        PATH_COMPONENT_MAP.put("/system/config", "/system/config/index");
        PATH_COMPONENT_MAP.put("/system/notice", "/system/notice/index");
        PATH_COMPONENT_MAP.put("/system/login-log", "/system/log/index");
        PATH_COMPONENT_MAP.put("/system/oper-log", "/system/log/index");
    }

    private final Converter converter;

    @Override
    public void add(SysMenuAddDTO addDto) {
        SysMenu entity = converter.convert(addDto, SysMenu.class);
        this.save(entity);
    }

    @Override
    public void update(SysMenuUpdateDTO updateDto) {
        SysMenu entity = converter.convert(updateDto, SysMenu.class);
        this.updateById(entity);
    }

    @Override
    public void delete(String id) {
        String[] ids = id.split(",");
        this.removeByIds(Arrays.asList(ids).stream().toList());
    }

    @Override
    public SysMenuVO findById(Long id) {
        SysMenu entity = this.getById(id);
        return converter.convert(entity, SysMenuVO.class);
    }

    @Override
    public List<RouterVO> buildRoutersForCurrentUser() {
        List<SysMenu> menus;
        if (AdminLoginHelper.getUserId().equals(CommonConstant.SUPER_ADMIN_ID)) {
            menus = this.list(
                    new LambdaQueryWrapper<SysMenu>()
                            .ne(SysMenu::getMenuType, MENU_TYPE_BUTTON)
                            .eq(SysMenu::getStatus, CommonConstant.STATUS_NORMAL)
                            .eq(SysMenu::getVisible, VISIBLE_SHOW)
                            .orderByAsc(SysMenu::getParentId)
                            .orderByAsc(SysMenu::getOrderNum)
            );
        } else {
            Long userId = AdminLoginHelper.getUserId();
            List<SysMenu> joined = this.selectJoinList(SysMenu.class,
                    new MPJLambdaWrapper<SysMenu>()
                            .selectAll(SysMenu.class)
                            .innerJoin(SysRoleMenu.class, SysRoleMenu::getMenuId, SysMenu::getMenuId)
                            .innerJoin(SysUserRole.class, SysUserRole::getRoleId, SysRoleMenu::getRoleId)
                            .eq(SysUserRole::getUserId, userId)
                            .ne(SysMenu::getMenuType, MENU_TYPE_BUTTON)
                            .eq(SysMenu::getStatus, CommonConstant.STATUS_NORMAL)
                            .eq(SysMenu::getVisible, VISIBLE_SHOW)
                            .orderByAsc(SysMenu::getParentId)
                            .orderByAsc(SysMenu::getOrderNum)
            );
            Set<Long> seen = new LinkedHashSet<>();
            menus = joined.stream().filter(m -> seen.add(m.getMenuId())).toList();
        }
        List<SysMenuVO> tree = buildTree(0L, menus.stream()
                .map(item -> converter.convert(item, SysMenuVO.class))
                .toList());
        return buildRouterTree(tree);
    }

    @Override
    public List<SysMenuVO> listTree(SysMenuQueryDTO queryDto) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(queryDto.getMenuName()), SysMenu::getMenuName, queryDto.getMenuName());
        wrapper.eq(StrUtil.isNotEmpty(queryDto.getStatus()), SysMenu::getStatus, queryDto.getStatus());
        wrapper.orderByAsc(SysMenu::getParentId).orderByAsc(SysMenu::getOrderNum);
        List<SysMenuVO> sysMenuVOList = this.list(wrapper).stream().map(item -> converter.convert(item, SysMenuVO.class)).toList();
        return buildTree(0L, sysMenuVOList);
    }

    private List<RouterVO> buildRouterTree(List<SysMenuVO> menuTree) {
        return menuTree.stream().map(this::menuToRouter).toList();
    }

    private RouterVO menuToRouter(SysMenuVO menu) {
        RouterVO vo = new RouterVO();
        vo.setPath(resolvePath(menu));
        vo.setName(resolveName(menu));
        vo.setComponent(resolveComponent(menu));

        RouterVO.Meta meta = new RouterVO.Meta();
        meta.setTitle(menu.getMenuName());
        meta.setIcon(menu.getIcon());
        meta.setIsHide(!VISIBLE_SHOW.equals(menu.getVisible()));
        meta.setIsKeepAlive(true);
        vo.setMeta(meta);

        if (CollectionUtil.isNotEmpty(menu.getChildren())) {
            List<RouterVO> children = buildRouterTree(menu.getChildren());
            vo.setChildren(children);
            if (MENU_TYPE_DIR.equals(menu.getMenuType()) && CollectionUtil.isNotEmpty(children)) {
                vo.setRedirect(children.get(0).getPath());
            }
        }
        return vo;
    }

    private String resolveName(SysMenuVO menu) {
        if (StrUtil.isNotBlank(menu.getRouteName())) {
            return menu.getRouteName();
        }
        return "Menu" + menu.getMenuId();
    }

    /**
     * 目录菜单库中 path 常为 null；前端 Vue Router / 侧栏 index 不能为 null。
     * 首页与前端 HOME_URL（/home/index）对齐。
     */
    private String resolvePath(SysMenuVO menu) {
        if ("/dashboard".equals(menu.getPath())) {
            return "/home/index";
        }
        if (StrUtil.isNotBlank(menu.getPath())) {
            return menu.getPath();
        }
        if (StrUtil.isNotBlank(menu.getRouteName())) {
            return "/" + menu.getRouteName();
        }
        return "/menu-" + menu.getMenuId();
    }

    /**
     * component 必须能对上 views 下 glob 路径；库中为空时按 path 推导或查映射表。
     */
    private String resolveComponent(SysMenuVO menu) {
        if (MENU_TYPE_DIR.equals(menu.getMenuType())) {
            return null;
        }
        if (StrUtil.isNotBlank(menu.getComponent())) {
            return menu.getComponent();
        }
        String dbPath = menu.getPath();
        if (StrUtil.isBlank(dbPath)) {
            return null;
        }
        String mapped = PATH_COMPONENT_MAP.get(dbPath);
        if (StrUtil.isNotBlank(mapped)) {
            return mapped;
        }
        return dbPath.endsWith("/index") ? dbPath : dbPath + "/index";
    }

    private List<SysMenuVO> buildTree(long parentId, List<SysMenuVO> all) {
        return all.stream()
                .filter(item -> item.getParentId() != null && item.getParentId().equals(parentId))
                .peek(item -> item.setChildren(buildTree(item.getMenuId(), all)))
                .toList();
    }
}
