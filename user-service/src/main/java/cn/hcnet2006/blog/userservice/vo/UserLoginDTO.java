package cn.hcnet2006.blog.userservice.vo;

import cn.hcnet2006.blog.userservice.bean.SysUser;

public class UserLoginDTO {
    private JWT jwt;
    private SysUser userDetails;

    public JWT getJwt() {
        return jwt;
    }

    public void setJwt(JWT jwt) {
        this.jwt = jwt;
    }

    public SysUser getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(SysUser userDetails) {
        this.userDetails = userDetails;
    }
}
