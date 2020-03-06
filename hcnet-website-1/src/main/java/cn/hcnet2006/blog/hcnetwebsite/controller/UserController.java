package cn.hcnet2006.blog.hcnetwebsite.controller;

import cn.hcnet2006.blog.hcnetwebsite.bean.SysUser;
import cn.hcnet2006.blog.hcnetwebsite.http.HttpResult;
import cn.hcnet2006.blog.hcnetwebsite.jwt.UserLoginDTO;
import cn.hcnet2006.blog.hcnetwebsite.pages.PageRequest;
import cn.hcnet2006.blog.hcnetwebsite.pages.PageResult;
import cn.hcnet2006.blog.hcnetwebsite.service.SysUserService;
import cn.hcnet2006.blog.hcnetwebsite.util.OSSUtils;
import cn.hcnet2006.blog.hcnetwebsite.util.PassWordEncoderUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Api(tags = "用户信息接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private SysUserService sysUserService;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //private String url = "/usr/local/spring";
    @ApiOperation(value = "用户登录",notes = "用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "name", value = "用户名",required = true),
            @ApiImplicitParam(type = "query", name = "password", value = "密码",required = true)
    })
    @PostMapping("/login")
    public HttpResult login(String name, String password) throws LoginException {
        //获取用户信息
        UserLoginDTO result  =sysUserService.login(name,password);
        return HttpResult.ok(result);
    }

    @ApiOperation(value = "用户注册",notes = "用户注册" +
            "参数包括：" +
            "1.")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name = "name",value = "用户名",required = true),
            @ApiImplicitParam(type = "query", name = "password",value = "密码",required = true),
            @ApiImplicitParam(type = "query", name = "deptId",value = "所属方向ID",required = true),
            @ApiImplicitParam(type = "query", name = "grade",value = "年级，比如2018",required = true),
            @ApiImplicitParam(type = "query", name = "email",value = "邮箱，确保格式正确",required = true),
            @ApiImplicitParam(type = "query", name = "mobile",value = "手机，确保格式正确",required = true),
            @ApiImplicitParam(type = "query", name = "createBy",value = "创建者",required = true),
            //@ApiImplicitParam(type = "query", name = "createTime",value = "创建时间",required = true)
    })
    @PostMapping("/register")
    //@PreAuthorize("hasAuthority('ROLE_USER')")
    public HttpResult register(SysUser sysUser, @ApiParam(value = "uploadFile", required = true) MultipartFile uploadFile,
                               HttpServletRequest request) throws FileNotFoundException {

        //新建暂时缓存目录,该目录一定存在
        String url = ResourceUtils.getURL("").getPath()+uploadFile.getOriginalFilename();
        System.out.println(url);
        File folder = new File(url);
        try{
            //转义文件到服务器
            uploadFile.transferTo(folder);
            //从服务器获取文件传递到阿里云OSS.返回下载链接地址
            String avator_url = OSSUtils.upload(folder,sysUser.getName()+".jpg");
            //删除服务器缓存文件
            folder.delete();
            //设置属性
            //设置用户头像
            sysUser.setAvator(avator_url);
            //设置创建时间
            sysUser.setCreateTime(new Date());
            //设置更新时间
            sysUser.setLastUpdateTime(new Date());
            //设置创建者
            sysUser.setLastUpdateBy(sysUser.getCreateBy());
            //删除标志
            sysUser.setDelFlag((byte)0);
            //密码加密
            sysUser.setPassword(PassWordEncoderUtils.BCryptPassword(sysUser.getPassword()));
            //保存
            System.out.println("time:"+sdf.format(new Date()));
            sysUserService.save(sysUser);
            return HttpResult.ok(sysUser);
        }catch (DuplicateKeyException e){
            return HttpResult.error("重复注册");
        }catch (IOException e){
            e.printStackTrace();
            return HttpResult.error("注册失败");
        }

    }
    @ApiOperation(value = "用户修改",notes = "用户修改")
    @ApiImplicitParams({
            @ApiImplicitParam(type = "query", name="id",value = "用户编号",required = true),
            @ApiImplicitParam(type = "query", name = "name",value = "用户名"),
            @ApiImplicitParam(type = "query", name = "password",value = "密码"),
            @ApiImplicitParam(type = "query", name = "deptId",value = "所属方向ID"),
            @ApiImplicitParam(type = "query", name = "grade",value = "年级，比如2018"),
            @ApiImplicitParam(type = "query", name = "email",value = "邮箱，确保格式正确"),
            @ApiImplicitParam(type = "query", name = "mobile",value = "手机，确保格式正确"),
            @ApiImplicitParam(type = "query", name = "lastUpdateBy",value = "修改者"),
            @ApiImplicitParam(type = "query", name = "delFlag",value = "删除标志，-1删除，0正常")
            //@ApiImplicitParam(type = "query", name = "createTime",value = "创建时间",required = true)
    })
    @PutMapping("/update")
    public HttpResult update(SysUser sysUser, @ApiParam(value = "uploadFile",required = false) MultipartFile uploadFile) throws IOException,NullPointerException {
        try{
            if (uploadFile !=null){
                String url = ResourceUtils.getURL("").getPath()+uploadFile.getOriginalFilename();
                File folder = new File(url);
                uploadFile.transferTo(folder);
                String avator_url = OSSUtils.upload(folder, UUID.randomUUID().toString() +".jpg");
                folder.delete();
                sysUser.setAvator(avator_url);
            }
            sysUser.setLastUpdateTime(new Date());
            sysUserService.update(sysUser);
            return HttpResult.ok(sysUser);
        }catch (Exception e) {
            e.printStackTrace();
            return HttpResult.error("用户修改失败");
        }
    }
    @ApiOperation(value = "分页查看用户列表",notes = "分页查看用户列表:可选参数列表，以and的形式，随机组合，不加参数就是全选\n" +
            "@ApiImplicitParam(type = \"query\", name=\"id\",value = \"用户编号\",required = true),\n" +
            "            @ApiImplicitParam(type = \"query\", name = \"name\",value = \"用户名\"),\n" +
            "            @ApiImplicitParam(type = \"query\", name = \"deptId\",value = \"所属方向ID\"),\n" +
            "            @ApiImplicitParam(type = \"query\", name = \"grade\",value = \"年级，比如2018\"),\n" +
            "            @ApiImplicitParam(type = \"query\", name = \"delFlag\",value = \"删除标志，-1删除，0正常\")")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "pageNum", value = "当前页码",required = true),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页行数",required = true),
    })
    @PostMapping("/find/page")
    public HttpResult find(int pageNum, int pageSize, @RequestBody SysUser sysUser){
        Map<String, Object> map = new HashMap<>();
        map.put("sysUser",sysUser);
        PageRequest pageRequest = new PageRequest(pageNum, pageSize, map);
        PageResult pageResult = sysUserService.findPage(pageRequest);
        return HttpResult.ok(pageResult);
    }
}
