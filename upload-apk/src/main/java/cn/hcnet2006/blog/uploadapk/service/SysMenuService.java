package cn.hcnet2006.blog.uploadapk.service;



import cn.hcnet2006.blog.uploadapk.bean.SysMenu;
import cn.hcnet2006.blog.uploadapk.page.PageRequest;
import cn.hcnet2006.blog.uploadapk.page.PageResult;

import java.util.List;

/**
 * 菜单管理
 */

public interface SysMenuService extends CurdService<SysMenu>{
    /**
     * 根据用户名查找菜单列表
     * @param username
     * @return
     */
    List<SysMenu> findByUser(String username);
    @Override
    default int save(SysMenu record) {
        return 0;
    }

    @Override
    default int delete(SysMenu record) {
        return 0;
    }

    @Override
    default int delete(List<SysMenu> records) {
        return 0;
    }

    @Override
    default SysMenu findById(Long id) {
        return null;
    }

    @Override
    default PageResult findPage(PageRequest pageRequest) {
        return null;
    }
}
