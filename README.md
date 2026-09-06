# 取件码助手 (ParcelCode)

一款专注于快递取件码高效归类、快速查看的 Android 极简工具。

---

## ✨ 功能特性

1. **剪切板智能识别**：复制菜鸟驿站、丰巢快递柜、兔喜生活、顺丰速运等快递短信后，打开应用自动弹出提取确认，智能分离取件码与快递单号。
2. **左滑标记取件完成**：取件卡片支持向左滑动（Swipe-to-Dismiss）标记已取件，并丝滑自动切换至下一个未取取件码。
3. **极简空状态呈现**：所有包裹处理完毕或当前无包裹时，居中显示“**您目前无包裹**”。
4. **大字号大气排版**：重点突出 32sp+ 超大取件码与货架位置，减少冗余文字干扰，一眼看清。
5. **背景颜色跟随系统**：原生支持 Android 深浅色主题自适应，白天清爽高对比，夜间深色护眼省电。
6. **桌面小组件（App Widget）**：支持将待取件数与最新取件码放置于桌面，随时查阅无需进入应用。

---

## 🚀 GitHub Releases 自动打包发布流程

本项目已配置完善的 **GitHub Actions 自动化发布工作流**（位于 `.github/workflows/release.yml`）。当您将代码上传至 GitHub 仓库后，可以通过以下两种方式一键生成 GitHub Releases 及 APK 安装包：

### 方式一：网页端一键打包发布（推荐）
1. 打开您的 GitHub 项目主页。
2. 点击顶部导航栏的 **「Actions」** 选项卡。
3. 在左侧工作流列表中选择 **「Build & Publish GitHub Release」**。
4. 点击右侧的 **「Run workflow」** 按钮：
   - 输入发布版本号（默认 `v1.0.0`）
   - 输入 Release 标题
   - 点击绿色的 **「Run workflow」** 触发。
5. 等待 1~2 分钟，构建完成后前往项目主页右侧的 **「Releases」** 栏，即可直接下载打包好的正式版 APK（`ParcelCode-v1.0.0-release.apk`）。

### 方式二：通过 Git Tag 自动触发
在本地终端给仓库打上版本 Tag 并推送至 GitHub：
```bash
git tag v1.0.0
git push origin v1.0.0
```
GitHub Actions 检测到 `v*` 开头的 Tag 后，会自动执行构建、打包并在 GitHub Releases 页面发布该版本及对应的 APK 安装包。

---

## 🛠️ 本地编译与运行

- **调试构建**：
  ```bash
  ./gradlew assembleDebug
  ```
- **本地发布打包**：
  ```bash
  ./build_release.sh
  # 或执行
  ./gradlew assembleRelease
  ```
  生成的 APK 位于：`app/build/outputs/apk/release/`
- **运行单元测试**：
  ```bash
  ./gradlew testDebugUnitTest
  ```
