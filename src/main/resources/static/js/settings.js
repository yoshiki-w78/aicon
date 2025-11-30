// - Geminiモデル選択（サーバー管理キー前提）
// - 画像生成プロバイダ選択
// - 選択内容は localStorage に保存し、フロント側で利用

const GEMINI_MODEL_KEY = "aicon_gemini_model";
const IMAGE_PROVIDER_KEY = "aicon_image_provider";

document.addEventListener("DOMContentLoaded", () => {
  const logoutBtn = document.getElementById("logout-btn");
  const modelSelect = document.getElementById("gemini-model-select");
  const loadModelsBtn = document.getElementById("load-models-btn");
  const modelStatus = document.getElementById("model-status");
  const providerSelect = document.getElementById("image-provider-select");

  /* ログアウト（必要なら実装を差し替え） */
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      localStorage.removeItem("aicon_token");
      window.location.href = "/";
    });
  }

  /* 画像プロバイダ選択 初期値ロード */
  if (providerSelect) {
    const savedProvider = localStorage.getItem(IMAGE_PROVIDER_KEY) || "PLACEHOLDER";
    providerSelect.value = savedProvider;

    providerSelect.addEventListener("change", () => {
      localStorage.setItem(IMAGE_PROVIDER_KEY, providerSelect.value);
    });
  }

  /* Geminiモデル 初期値セット（一覧読み込み前は値だけ保持） */
  const savedModelId = localStorage.getItem(GEMINI_MODEL_KEY) || "";

  /* モデル一覧を取得（サーバーの /api/gemini/models を想定） */
  if (loadModelsBtn && modelSelect && modelStatus) {
    loadModelsBtn.addEventListener("click", async () => {
      modelStatus.textContent = "モデル一覧を取得中...";
      loadModelsBtn.disabled = true;

      try {
        const res = await fetch("/api/gemini/models");
        if (!res.ok) {
          throw new Error("HTTP " + res.status);
        }

        const models = await res.json();
        if (!Array.isArray(models) || models.length === 0) {
          modelStatus.textContent = "利用可能なモデルが取得できませんでした。サーバー設定を確認してください。";
          return;
        }

        // セレクト初期化
        modelSelect.innerHTML = "";
        // デフォルト: 未指定項目
        const optEmpty = document.createElement("option");
        optEmpty.value = "";
        optEmpty.textContent = "（未指定：サーバー側のデフォルトモデルを使用）";
        modelSelect.appendChild(optEmpty);

        models.forEach((m) => {
          // m: { modelId, displayName, recommended }
          const opt = document.createElement("option");
          opt.value = m.modelId || "";
          const label = m.displayName || m.modelId || "";
          opt.textContent = m.recommended ? `${label}（推奨）` : label;

          // 保存済みモデルなら選択状態に
          if (savedModelId && savedModelId === opt.value) {
            opt.selected = true;
          }

          modelSelect.appendChild(opt);
        });

        modelStatus.textContent = "モデル一覧を読み込みました。使用したいモデルを選択してください。";
      } catch (err) {
        console.error(err);
        modelStatus.textContent =
          "モデル一覧の取得に失敗しました。Geminiキーや /api/gemini/models の実装を確認してください。";
      } finally {
        loadModelsBtn.disabled = false;
      }
    });
  }

  /* モデル選択変更時に保存 */
  if (modelSelect && modelStatus) {
    modelSelect.addEventListener("change", () => {
      const value = modelSelect.value || "";
      if (value) {
        localStorage.setItem(GEMINI_MODEL_KEY, value);
        modelStatus.textContent = `選択中のモデル: ${value}`;
      } else {
        localStorage.removeItem(GEMINI_MODEL_KEY);
        modelStatus.textContent = "モデル未指定：サーバー側デフォルトを使用します。";
      }
    });
  }

  // 既に保存済みモデルがある場合、その旨だけ表示（一覧ロード前）
  if (savedModelId && modelStatus) {
    modelStatus.textContent =
      `保存済みモデルID: ${savedModelId}（「モデル一覧を取得」で表示名を確認できます）`;
  }
});