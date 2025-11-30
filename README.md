# AIcon – AIアイコン自動生成　ハッカソン

[![Java](https://img.shields.io/badge/Java-17-007396)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-6DB33F)](#)
[![DB](https://img.shields.io/badge/DB-H2--in--memory-blue)](#)
[![Build](https://img.shields.io/badge/Build-Maven-orange)](#)

**AIcon** は、テキストプロンプトからアイコン画像を生成・プレビューするデモアプリです。  
- サーバ：**Java 17 / Spring Boot 3.5**  
- フロント：`/static` のシンプルなHTML/JS  
- 画像生成：OpenAI Images API（※デフォルトは**デモ画像モード**でプレースホルダを表示）

> **Note for reviewers**  
> 本リポジトリはデモ用として、**既定では実画像生成を無効化**し、プレースホルダ画像を**意図的に**返します。  
> 実画像生成を有効にする手順は下記「設定（画像生成の有効化）」をご参照ください。

### 参加イベント
- **イベント:** 【技育CAMP2025】ハッカソン Vol.14
- **主催:** 株式会社サポーターズ
- **日程:** キックオフ 2025/10/31 19:00–19:30  
           本戦 2025/11/08 11:00 – 2025/11/09 19:30
- **チーム:** ショートカットサバイバル
- **取り組み:** AIcon（AIアイコン生成デモ）を開発し、  
  ・プロンプト生成の土台、  
  ・OpenAI Images API 連携の**デモ画像モード（意図的なプレースホルダ返却）**、  
  ・UI/サイズバリエーション（1024x1024/1024x1536/1536x1024）  
  を担当・実装。発表までを通して、短時間で動く価値を出すことに注力しました。

---


## 1. できること（Features）

- ✅ チャット風UIでプロンプト作成（Gemini連携箇所は**デモ**実装）
- ✅ 画像生成API呼び出し（**デフォルトはダミー画像**を返す安全設計）
- ✅ 正方形 / 横長 / 縦長の3アスペクトに対応（1024x1024 / 1024x1536 / 1536x1024）
- ✅ H2（インメモリ）で動作、セットアップが簡単

---

## 2. スクリーンショット
![AIcon UI – ホーム画面](docs/screenshots/aicon-demo.png)

---

## 3. クイックスタート（デモ画像モード）

```bash
# 依存ダウンロード & 起動
./mvnw spring-boot:run

# ブラウザで
http://localhost:8080
```
- 何も設定しなくても プレースホルダ画像 が表示されます（デモ安全モード）。
- 実画像生成を使いたい場合は「4. 設定」をご覧ください。

---


## 4. 設定（画像生成の有効化）

本アプリは `image.provider` により挙動を切り替えます。

- `PLACEHOLDER`（既定）：ダミー画像（プレースホルダ）を返す  
- `OPENAI`：OpenAI Images API を呼び出す

### 4.1 application.properties（例）

```properties
# アプリ名
spring.application.name=aicon

# H2 (インメモリ)
spring.datasource.url=jdbc:h2:mem:aicondb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

# --- 画像生成プロバイダ ---
# デモ中は PLACEHOLDER のまま（＝本番キー不要＆安全）
image.provider=PLACEHOLDER

# OpenAI を使う場合のみ設定（有効化手順）
# image.provider=OPENAI
# openai.api.key=${OPENAI_API_KEY:}
# openai.image.model=dall-e-3
#   - 例: dall-e-3 / gpt-image-1 / gpt-image-1-mini
#   - gpt-image-1/mini は組織の利用要件がある場合あり

# --- Gemini（プロンプト支援：デモ実装） ---
# gemini.api.key=${GEMINI_API_KEY:}
# gemini.model=gemini-2.0-flash

# --- Stripe（任意：未設定で無効） ---
stripe.secret-key=${STRIPE_SECRET_KEY:}
stripe.price-id-pro=${STRIPE_PRICE_ID_PRO:}
stripe.webhook-secret=${STRIPE_WEBHOOK_SECRET:}

# --- GitHub OAuth（任意：未設定で無効） ---
# spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID:}
# spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET:}
# spring.security.oauth2.client.registration.github.scope=read:user,user:email
# spring.security.oauth2.client.registration.github.client-name=GitHub
# spring.security.oauth2.client.registration.github.redirect-uri="{baseUrl}/login/oauth2/code/github"
# spring.security.oauth2.client.provider.github.authorization-uri=https://github.com/login/oauth/authorize
# spring.security.oauth2.client.provider.github.token-uri=https://github.com/login/oauth/access_token
# spring.security.oauth2.client.provider.github.user-info-uri=https://api.github.com/user
# spring.security.oauth2.client.provider.github.user-name-attribute=id
```

### 4.2 OpenAIを有効化する手順

1. `image.provider=OPENAI` に変更  
2. `openai.api.key` に API Key を設定（`.env` 経由推奨）  
3. `openai.image.model` に使用モデルを指定（例：`dall-e-3`）  
4. **制約メモ**
   - DALL·E 3 は `n=1` 固定が安全
   - `size` は `1024x1024` / `1024x1536` / `1536x1024` を使用
   - 一部モデルは**組織の検証（Verify）**が必要な場合あり

---

## 5.API

- `POST /api/icons/generate`
  - Request(JSON): `prompt`(必須), `count`(1固定を推奨), `size`(`1024x1024` / `1024x1536` / `1536x1024`), `style`(任意)
  - Response(JSON): `icons: [{ id, imageUrl }]`

### cURL（デモ画像モードでも動作）
```bash
curl -X POST http://localhost:8080/api/icons/generate \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Futuristic phoenix logo, minimal, gradient",
    "count": 1,
    "size": "1024x1024",
    "style": "flat, gradient"
  }'
```



### `.env.example`（鍵は絶対コミットしない）

```markdown
## .env.example
# デモ画像モード（既定）
IMAGE_PROVIDER=PLACEHOLDER

# OpenAIを使う場合（→ READMEの「4.2」を参照）
OPENAI_API_KEY=
OPENAI_IMAGE_MODEL=dall-e-3

# 任意（デモ実装）
GEMINI_API_KEY=
GEMINI_MODEL=gemini-2.0-flash

# 任意（未設定で無効）
STRIPE_SECRET_KEY=
STRIPE_PRICE_ID_PRO=
STRIPE_WEBHOOK_SECRET=
GITHUB_CLIENT_ID=
GITHUB_CLIENT_SECRET=
```

---

## 6.開発メモ（ローカル）
- 起動: `./mvnw spring-boot:run`
- アプリ: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console  
  - JDBC: `jdbc:h2:mem:aicondb` / User: `sa` / Password: 空
 
---

## 7.今後の展望
- 生成結果のギャラリー保存（DB化/永続化）
- タグ/お気に入り/ダウンロード
- 課金（Stripe）による生成回数拡張
- CI/CD & 本番デプロイ

---

## 8.License & Credits
- Code: MIT
- Frameworks: Spring Boot (Apache-2.0), H2 (MPL-2.0) など
- Images API: OpenAI (利用規約に従う)
- Prompt Assist API: Google AI Studio – Gemini (利用規約に従う)
- Hackathon: 【技育CAMP2025】Vol.14 / チーム「ショートカットサバイバル」
- ※記載の各サービス名・ロゴは各社の商標です。プロジェクトは各社の承認・提携を意味しません。
