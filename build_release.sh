#!/usr/bin/env bash
# ==============================================================================
# 一键打包 Release APK 脚本 (One-Click Release Packaging Script)
# ==============================================================================
set -e

echo "========================================================"
echo "📦 正在执行：取件码助手 - 一键打包 Release APK"
echo "========================================================"
echo ""

export STORE_PASSWORD="${STORE_PASSWORD:-android}"
export KEY_PASSWORD="${KEY_PASSWORD:-android}"

# 执行 assembleRelease
gradle :app:assembleRelease --no-daemon

echo ""
echo "========================================================"
echo "✅ Release APK 构建打包成功！"
echo "📁 输出目录: app/build/outputs/apk/release/"
echo "========================================================"
ls -lh app/build/outputs/apk/release/ || true
