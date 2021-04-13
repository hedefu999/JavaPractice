springboot vue 微人事前后端分离项目

来自《springboot vue实战》

SPA-Single Page Application单页应用

# 环境准备
安装node： `brew install node@12`
添加到path `echo 'export PATH="/usr/local/opt/node@12/bin:$PATH"' >> ~/.zshrc`
    编译人员再加个  `export LDFLAGS="-L/usr/local/opt/node@12/lib"`  `export CPPFLAGS="-I/usr/local/opt/node@12/include"`
修改npm源为淘宝源  `npm config set registry http://registry.npm.taobao.org/`
    改回node官方源   `npm config set registry https://registry.npmjs.org/`
全局安装vue-cli `npm install -g @vue/cli`
初始化工具 vue init 安装    `npm install -g @vue/cli-init`

# 搭建vue项目
初始化  `vue init webpack folderName`
随后设置 是否覆盖已存在的目录、项目的名字、项目描述、作者、标准还是压缩的构建方式、安装vue路由、是否使用ESLint做代码检查、是否使用单元测试、是否使用Nightwatche2e测试

运行项目 `cd folderName`  `npm run dev`


# 准备后台项目
使用spring-boot-security做登录
实体类 Hr 实现 org.springframework.security.core.userdetails.UserDetails 接口
hr实体的角色信息 包装为 org.springframework.security.core.authority.SimpleGrantedAuthority返回
先做完简单的登录

# 前端项目开发
安装组件库Element-UI:  `npm i element-ui -S`, 在项目根目录下执行
安装基于promise的HTTP库 axios： `npm install axios`