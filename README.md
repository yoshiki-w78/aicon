# AIcon – AIアイコン自動生成（Spring Boot）

[![Java](https://img.shields.io/badge/Java-17-007396)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-6DB33F)](#)
[![DB](https://img.shields.io/badge/DB-H2--in--memory-blue)](#)
[![Build](https://img.shields.io/badge/Build-Maven-orange)](#)

**AIcon** は、テキストプロンプトからアイコン画像を生成・プレビューする学習用/就活向けデモアプリです。  
- サーバ：**Java 17 / Spring Boot 3.5**  
- フロント：`/static` のシンプルなHTML/JS  
- 画像生成：OpenAI Images API（※デフォルトは**デモ画像モード**でプレースホルダを表示）

> **Note for reviewers**  
> 本リポジトリはデモ用として、**既定では実画像生成を無効化**し、プレースホルダ画像を**意図的に**返します。  
> 実画像生成を有効にする手順は下記「設定（画像生成の有効化）」をご参照ください。

---

## 1. できること（Features）

- ✅ チャット風UIでプロンプト作成（Gemini連携箇所は**デモ**実装）
- ✅ 画像生成API呼び出し（**デフォルトはダミー画像**を返す安全設計）
- ✅ 正方形 / 横長 / 縦長の3アスペクトに対応（1024x1024 / 1024x1536 / 1536x1024）
- ✅ H2（インメモリ）で動作、セットアップが簡単

---

## 2. スクリーンショット



---

## 3. クイックスタート（デモ画像モード）

```bash
# 依存ダウンロード & 起動
./mvnw spring-boot:run

# ブラウザで
http://localhost:8080
