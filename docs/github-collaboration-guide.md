# GitHub 团队协作开发指南

> 本文档面向 GitHub 新手，从零开始讲解如何使用 GitHub 进行团队协作开发。

---

## 目录

1. [GitHub 是什么](#1-github-是什么)
2. [为什么要用 GitHub](#2-为什么要用-github)
3. [准备工作](#3-准备工作)
4. [获取项目代码](#4-获取项目代码)
5. [日常开发流程](#5-日常开发流程)
6. [提交代码到 GitHub](#6-提交代码到-github)
7. [团队协作流程](#7-团队协作流程)
8. [常见问题解答](#8-常见问题解答)
9. [常用命令速查表](#9-常用命令速查表)

---

## 1. GitHub 是什么

### 简单理解

**GitHub = 代码的网盘 + 版本历史 + 团队协作平台**

想象一下：
- **网盘**：把代码存到网上，随时随地都能下载
- **版本历史**：每次修改都有记录，可以回到任意版本
- **团队协作**：多人可以同时修改同一个项目，互不干扰

### 生活中的例子

| 场景 | GitHub 对应 |
|------|-------------|
| 你写论文，改了很多版 | GitHub 记录每次修改，可以回退 |
| 多人合作写论文 | GitHub 让每个人改自己的部分，最后合并 |
| 论文存到网盘 | GitHub 把代码存到云端 |

---

## 2. 为什么要用 GitHub

### 对团队的好处

| 好处 | 说明 |
|------|------|
| **代码备份** | 代码存在云端，电脑坏了也不怕 |
| **版本控制** | 每次修改都有记录，出问题可以回退 |
| **多人协作** | 4个人可以同时开发，互不干扰 |
| **代码审查** | 提交代码前可以互相检查 |
| **项目管理** | 可以分配任务、追踪进度 |

### 对个人的好处

| 好处 | 说明 |
|------|------|
| **学习成长** | 看别人怎么写代码 |
| **作品集** | 找工作时展示你的项目 |
| **技能提升** | Git 是程序员必备技能 |

---

## 3. 准备工作

### 3.1 注册 GitHub 账号

#### 步骤 1：打开 GitHub 官网

浏览器访问：https://github.com

#### 步骤 2：点击 Sign Up（注册）

- **Username**：起一个用户名（英文，比如 `zhangsan`）
- **Email**：填写你的邮箱
- **Password**：设置密码

#### 步骤 3：验证邮箱

- GitHub 会发一封邮件到你的邮箱
- 打开邮箱，点击验证链接

#### 步骤 4：完成注册

注册成功后，你就有了自己的 GitHub 账号！

---

### 3.2 安装 Git

#### Windows 用户

1. **下载 Git**
   - 访问：https://git-scm.com/download/win
   - 点击下载，一路默认安装即可

2. **验证安装**
   - 打开命令提示符（Win + R，输入 `cmd`）
   - 输入：`git --version`
   - 看到版本号说明安装成功

#### Mac 用户

1. **打开终端**
   - 按 `Command + 空格`，搜索 `终端`

2. **安装 Git**
   - 输入：`xcode-select --install`
   - 按提示完成安装

---

### 3.3 配置 Git

打开命令提示符（Windows）或终端（Mac），执行以下命令：

```bash
# 配置你的用户名（改成你的名字）
git config --global user.name "你的名字"

# 配置你的邮箱（改成你的邮箱）
git config --global user.email "你的邮箱@example.com"
```

**注意**：这里的用户名和邮箱会显示在你的提交记录中，建议用真实信息。

---

## 4. 获取项目代码

### 4.1 项目仓库地址

我们项目的 GitHub 地址：https://github.com/YanShui9/Hangzhou

### 4.2 克隆项目（下载代码）

#### 步骤 1：选择存放位置

找一个你想放项目的文件夹，比如：
- Windows：`D:\projects\`
- Mac：`~/projects/`

#### 步骤 2：打开命令行

- Windows：在文件夹空白处按住 `Shift` + 右键，选择"在此处打开 PowerShell"
- Mac：打开终端，用 `cd` 命令进入文件夹

#### 步骤 3：执行克隆命令

```bash
git clone https://github.com/YanShui9/Hangzhou.git
```

执行后会看到：
```
Cloning into 'Hangzhou'...
remote: Enumerating objects: 120, done.
remote: Counting objects: 100% (120/120), done.
remote: Compressing objects: 100% (80/80), done.
Receiving objects: 100% (120/120), 500.00 KiB | 2.00 MiB/s, done.
```

#### 步骤 4：进入项目目录

```bash
cd Hangzhou
```

现在你就有了项目的完整代码！

---

### 4.3 项目目录结构

```
Hangzhou/
├── park-server/          # 后端代码（Java）
├── park-admin/           # 前端代码（Vue）
├── sql/                  # 数据库脚本
├── docs/                 # 文档
├── CLAUDE.md             # 项目说明
└── README.md             # 项目介绍
```

---

## 5. 日常开发流程

### 5.1 开发前的准备

每次开始开发前，先执行以下命令，确保代码是最新的：

```bash
# 1. 切换到 develop 分支（开发分支）
git checkout develop

# 2. 拉取最新代码
git pull origin develop
```

### 5.2 创建你的功能分支

**重要**：不要直接在 `develop` 分支上开发！要创建自己的功能分支。

```bash
# 创建并切换到你的功能分支
git checkout -b feature/你的功能名
```

**分支命名规范**：

| 类型 | 格式 | 示例 |
|------|------|------|
| 新功能 | `feature/功能名` | `feature/login` |
| 修复Bug | `fix/问题描述` | `fix/login-error` |
| 修改样式 | `style/描述` | `style/button-color` |

**示例**：
```bash
# 如果你要开发登录功能
git checkout -b feature/login

# 如果你要修复登录Bug
git checkout -b fix/login-error
```

### 5.3 开发代码

现在你可以开始写代码了！

- 后端代码在 `park-server/` 目录
- 前端代码在 `park-admin/` 目录

**开发过程中**：
- 可以随时保存代码
- 可以多次提交（小步快跑）
- 不用担心改坏代码（Git 可以回退）

---

## 6. 提交代码到 GitHub

### 6.1 查看修改状态

开发一段时间后，查看你改了哪些文件：

```bash
git status
```

会看到类似：
```
On branch feature/login
Changes not staged for commit:
  modified:   park-admin/src/views/login/index.vue
  modified:   park-server/src/main/java/com/park/auth/controller/AuthController.java
```

### 6.2 添加修改到暂存区

```bash
# 添加所有修改的文件
git add .

# 或者只添加特定文件
git add park-admin/src/views/login/index.vue
```

### 6.3 提交修改

```bash
git commit -m "feat: 完成登录功能"
```

**提交信息规范**：

| 类型 | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat: 完成登录功能` |
| `fix` | 修复Bug | `fix: 修复登录失败问题` |
| `style` | 样式修改 | `style: 调整按钮颜色` |
| `docs` | 文档更新 | `docs: 更新README` |
| `refactor` | 重构代码 | `refactor: 优化登录逻辑` |

### 6.4 推送到 GitHub

```bash
# 推送你的分支到 GitHub
git push origin feature/login
```

### 6.5 创建 Pull Request（合并请求）

#### 什么是 Pull Request？

**Pull Request = 请求把你的代码合并到主分支**

就像写论文：
- 你写了一章内容（你的分支）
- 请求老师审核（Pull Request）
- 老师同意后，合并到整篇论文（主分支）

#### 如何创建 Pull Request？

1. **打开 GitHub 仓库页面**
   - 访问：https://github.com/YanShui9/Hangzhou

2. **点击 Pull requests 标签**

3. **点击 New pull request 按钮**

4. **选择分支**
   - base: `develop`（目标分支）
   - compare: `feature/login`（你的分支）

5. **填写标题和描述**
   - 标题：简短描述你做了什么
   - 描述：详细说明你的修改内容

6. **点击 Create pull request**

7. **等待审核**
   - 队长或队友会审核你的代码
   - 可能会提出修改建议
   - 审核通过后，代码会被合并到 `develop` 分支

---

## 7. 团队协作流程

### 7.1 我们的分支策略

```
main        ← 生产分支（最终上线的代码）
    ↑
develop     ← 开发分支（日常开发的基准）
    ↑
feature/xxx ← 功能分支（每个人开发的功能）
```

### 7.2 协作流程图

```
1. 拉取最新代码（git pull）
      ↓
2. 创建功能分支（git checkout -b feature/xxx）
      ↓
3. 开发代码
      ↓
4. 提交代码（git commit）
      ↓
5. 推送到 GitHub（git push）
      ↓
6. 创建 Pull Request
      ↓
7. 队友审核代码
      ↓
8. 合并到 develop 分支
      ↓
9. 重复以上步骤
```

### 7.3 队长的职责

| 职责 | 说明 |
|------|------|
| **审核代码** | 检查队友提交的代码质量 |
| **合并代码** | 把审核通过的代码合并到 develop |
| **解决冲突** | 如果多人修改同一文件，帮忙解决冲突 |
| **发布版本** | 定期把 develop 合并到 main |

### 7.4 队员的职责

| 职责 | 说明 |
|------|------|
| **拉取代码** | 每天开始工作前先拉取最新代码 |
| **创建分支** | 在自己的分支上开发 |
| **提交代码** | 完成功能后提交并推送 |
| **创建 PR** | 请求队长审核合并 |

---

## 8. 常见问题解答

### Q1：我改了代码，但想撤销怎么办？

```bash
# 撤销某个文件的修改（未提交前）
git checkout -- 文件名

# 撤销所有修改
git checkout -- .
```

### Q2：我提交了错误的代码，想回退怎么办？

```bash
# 查看提交历史
git log --oneline

# 回退到上一次提交（保留修改）
git reset --soft HEAD~1

# 回退到上一次提交（丢弃修改）
git reset --hard HEAD~1
```

### Q3：多人修改同一文件，出现冲突怎么办？

**冲突示例**：
```
<<<<<<< HEAD
这是你的代码
=======
这是队友的代码
>>>>>>> feature/xxx
```

**解决方法**：
1. 打开冲突文件
2. 选择保留哪部分代码（或者合并两部分）
3. 删除 `<<<<<<<`、`=======`、`>>>>>>>` 这些标记
4. 保存文件
5. 重新提交

### Q4：我想同步队友的代码怎么办？

```bash
# 切换到 develop 分支
git checkout develop

# 拉取最新代码
git pull origin develop

# 切换回你的分支
git checkout feature/你的分支

# 合并 develop 的代码到你的分支
git merge develop
```

### Q5：我忘记切换分支，直接在 develop 上开发了怎么办？

```bash
# 1. 创建新分支（会保留当前修改）
git checkout -b feature/新分支名

# 2. 切换回 develop
git checkout develop

# 3. 撤销 develop 上的修改
git checkout -- .
```

### Q6：如何查看提交历史？

```bash
# 查看简洁的提交历史
git log --oneline

# 查看详细的提交历史
git log

# 查看某个文件的修改历史
git log --follow 文件名
```

### Q7：如何查看某个文件的修改内容？

```bash
# 查看工作区和暂存区的差异
git diff

# 查看暂存区和上次提交的差异
git diff --cached

# 查看工作区和上次提交的差异
git diff HEAD
```

---

## 9. 常用命令速查表

### 基础命令

| 命令 | 说明 |
|------|------|
| `git clone 地址` | 克隆远程仓库 |
| `git status` | 查看当前状态 |
| `git add .` | 添加所有修改到暂存区 |
| `git commit -m "说明"` | 提交修改 |
| `git push origin 分支名` | 推送到远程仓库 |
| `git pull origin 分支名` | 拉取远程代码 |

### 分支操作

| 命令 | 说明 |
|------|------|
| `git branch` | 查看本地分支 |
| `git branch -a` | 查看所有分支 |
| `git checkout 分支名` | 切换分支 |
| `git checkout -b 分支名` | 创建并切换分支 |
| `git merge 分支名` | 合并指定分支到当前分支 |
| `git branch -d 分支名` | 删除本地分支 |

### 查看历史

| 命令 | 说明 |
|------|------|
| `git log` | 查看完整提交历史 |
| `git log --oneline` | 查看简洁提交历史 |
| `git log --graph` | 查看分支合并图 |
| `git diff` | 查看未暂存的修改 |

### 撤销操作

| 命令 | 说明 |
|------|------|
| `git checkout -- 文件名` | 撤销工作区修改 |
| `git reset HEAD 文件名` | 撤销暂存区修改 |
| `git reset --soft HEAD~1` | 回退提交，保留修改 |
| `git reset --hard HEAD~1` | 回退提交，丢弃修改 |

---

## 10. 项目特定信息

### 仓库地址

- **GitHub 仓库**：https://github.com/YanShui9/Hangzhou
- **克隆命令**：`git clone https://github.com/YanShui9/Hangzhou.git`

### 分支说明

| 分支 | 用途 | 谁使用 |
|------|------|--------|
| `main` | 生产分支（上线代码） | 队长管理 |
| `develop` | 开发分支（日常开发） | 所有人 |
| `feature/xxx` | 功能分支 | 个人开发 |
| `fix/xxx` | 修复分支 | 个人修复 |

### 提交信息规范

```
类型: 简短描述

详细说明（可选）
```

**类型**：
- `feat`：新功能
- `fix`：修复Bug
- `style`：样式修改
- `docs`：文档更新
- `refactor`：重构代码
- `test`：测试相关
- `chore`：构建/工具变更

**示例**：
```
feat: 完成用户登录功能

- 实现用户名密码登录
- 添加JWT Token认证
- 添加登录表单验证
```

---

## 11. 开发环境配置

### 后端开发环境

1. **安装 JDK 8**
   - 下载：https://www.oracle.com/java/technologies/downloads/

2. **安装 Maven**
   - 下载：https://maven.apache.org/download.cgi
   - 配置环境变量

3. **安装 MySQL 8.0**
   - 下载：https://dev.mysql.com/downloads/mysql/

4. **配置数据库**
   - 创建数据库：`park_evaluation`
   - 导入数据：执行 `sql/init.sql`

5. **复制配置文件**
   ```bash
   cp park-server/src/main/resources/application-dev.yml.example park-server/src/main/resources/application-dev.yml
   ```
   - 修改 `application-dev.yml` 中的数据库密码

6. **启动后端**
   ```bash
   cd park-server
   mvn spring-boot:run
   ```

### 前端开发环境

1. **安装 Node.js**
   - 下载：https://nodejs.org/

2. **安装依赖**
   ```bash
   cd park-admin
   npm install
   ```

3. **启动前端**
   ```bash
   npm run serve
   ```

4. **访问项目**
   - 前端：http://localhost:8081
   - 后端：http://localhost:8080
   - Swagger文档：http://localhost:8080/swagger-ui.html

---

## 12. 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 市级管理员 | admin | 123456 |
| 区县管理员 | district | 123456 |
| 园区管理员 | park | 123456 |

---

## 13. 获取帮助

### 遇到问题怎么办？

1. **先看本文档**：很多问题都有解答
2. **搜索错误信息**：把错误信息复制到百度/Google搜索
3. **问队长**：如果解决不了，问队长
4. **问队友**：互相帮助

### 常用资源

- **GitHub 官方文档**：https://docs.github.com
- **Git 命令速查**：https://education.github.com/git-cheat-sheet-education.pdf
- **Markdown 语法**：https://www.markdownguide.org/basic-syntax

---

## 总结

### 核心流程

```
1. 拉取最新代码（git pull）
      ↓
2. 创建功能分支（git checkout -b feature/xxx）
      ↓
3. 开发代码
      ↓
4. 提交代码（git add . → git commit -m "xxx"）
      ↓
5. 推送到 GitHub（git push origin feature/xxx）
      ↓
6. 创建 Pull Request
      ↓
7. 等待审核合并
```

### 记住这几句话

- **开发前**：先拉取最新代码
- **开发时**：在自己的分支上开发
- **开发后**：提交并推送，创建 Pull Request
- **遇到冲突**：不要慌，找队长帮忙

---

**祝大家开发顺利！** 🚀

如有问题，随时问队长或队友。
