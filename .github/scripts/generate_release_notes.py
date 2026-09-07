#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import sys

def main():
    version_tag = os.environ.get("VERSION_TAG", "v1.2.0")
    clean_version = os.environ.get("APP_VERSION_NAME", "1.2.0")
    version_code = os.environ.get("APP_VERSION_CODE", "10200")
    apk_file = os.environ.get("APK_FILE", "ParcelCode-v1.2.0-release.apk")
    apk_filename = os.path.basename(apk_file)
    apk_size = os.environ.get("APK_SIZE", "未知")
    apk_sha256 = os.environ.get("APK_SHA256", "未知")
    custom_notes = os.environ.get("CUSTOM_NOTES", "").strip()

    notes_content = ""

    # 1. 优先使用工作流网页输入的更新说明
    if custom_notes:
        notes_content = custom_notes
    # 2. 从项目 CHANGELOG.md 中自动提取对应版本的更新日志
    elif os.path.exists("CHANGELOG.md"):
        try:
            with open("CHANGELOG.md", "r", encoding="utf-8") as f:
                lines = f.readlines()
            capturing = False
            extracted = []
            for line in lines:
                if not capturing:
                    if line.startswith("## ") and (f"[{version_tag}]" in line or f"[{clean_version}]" in line):
                        capturing = True
                        continue
                else:
                    if line.startswith("## ") or line.strip() == "---":
                        break
                    extracted.append(line)
            notes_content = "".join(extracted).strip()
        except Exception as e:
            print("Warning: reading CHANGELOG.md failed:", e)

    # 3. 兜底回退
    if not notes_content:
        notes_content = "- ✨ 修复已知问题，优化使用体验，支持直接覆盖更新安装。"

    body = f"""## 📦 取件码助手 {version_tag} 正式发布

### 📝 本次更新内容
{notes_content}

---

### 🛡️ 安装升级说明
- **直接覆盖安装**：本安装包由永久固定官方签名密钥签名，可**直接覆盖安装**旧版本升级，已有待取包裹和数据完全保留，无需卸载！
- **系统要求**：支持 Android 8.0 (API 26) 及更高版本（完美适配 Android 12 ~ 15 深浅主题自适应）。

---

### 🔍 安装包校验信息
| 属性 | 参数值 |
| :--- | :--- |
| **安装包文件名** | `{apk_filename}` |
| **应用版本 (versionName)** | `v{clean_version}` |
| **内部构建号 (versionCode)** | `{version_code}` |
| **文件大小** | `{apk_size}` |
| **SHA-256 校验码** | `{apk_sha256}` |

---

### ✨ 核心功能亮点
- **剪切板自动识别提取**：复制菜鸟、丰巢、兔喜等快递短信后打开 APP 自动识别取件码与单号
- **左滑标记取件完成**：取件码卡片向左滑动完成取件，丝滑自动切换下一条取件码
- **多套艺术配色方案**：莫兰迪、莫奈花园、极简深海等高阶艺术调色，支持实时预览与记忆
- **无包裹极简状态**：全部取件完毕或暂无包裹时，居中显示“您目前无包裹”
- **大字体极简设计**：超大字号突出核心取件码，降低视觉噪音
- **系统主题深浅跟随**：背景底色自适应跟随 Android 系统深色/浅色模式切换
- **桌面小组件支持**：桌面即刻一览待取件数量与最新取件码
"""

    with open("release_body.md", "w", encoding="utf-8") as f:
        f.write(body)

    print("Generated release_body.md successfully.")

if __name__ == "__main__":
    main()
