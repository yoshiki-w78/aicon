// DOM 要素取得
const chatWindow = document.getElementById("chat-window");
const chatInput = document.getElementById("chat-input");
const chatSendBtn = document.getElementById("chat-send-btn");

const finalPromptEl = document.getElementById("final-prompt");
const sizeSelect = document.getElementById("size-select");
const styleSelect = document.getElementById("style-select");
const resetPromptBtn = document.getElementById("reset-prompt-btn");

const generateBtn = document.getElementById("generate-btn");
const iconsGrid = document.getElementById("icons-grid");
const downloadBtn = document.getElementById("download-btn");
const errorMsg = document.getElementById("error-msg");

// 選択中アイコン
let selectedIconUrl = null;

// チャット表示系ヘルパー
function appendMessage(role, text) {
  if (!chatWindow) return;

  const row = document.createElement("div");
  row.classList.add("chat-row", role === "user" ? "user" : "assistant");

  const avatar = document.createElement("div");
  avatar.classList.add("chat-avatar", role === "user" ? "user" : "assistant");
  avatar.textContent = role === "user" ? "You" : "AI";

  const bubble = document.createElement("div");
  bubble.classList.add("chat-message", role === "user" ? "user" : "assistant");
  bubble.textContent = text;

  row.appendChild(avatar);
  row.appendChild(bubble);
  chatWindow.appendChild(row);

  chatWindow.scrollTop = chatWindow.scrollHeight;
}

// 英語プロンプト案用（提案ブロック）
function appendPromptSuggestion(promptText) {
  if (!chatWindow) return;

  const row = document.createElement("div");
  row.classList.add("chat-row", "assistant");

  const avatar = document.createElement("div");
  avatar.classList.add("chat-avatar", "assistant");
  avatar.textContent = "AI";

  const block = document.createElement("div");
  block.classList.add("chat-prompt-block");

  const label = document.createElement("div");
  label.classList.add("chat-prompt-label");
  label.textContent = "英語プロンプト案";

  const promptDiv = document.createElement("div");
  promptDiv.classList.add("chat-prompt-text");
  promptDiv.textContent = promptText;

  const btn = document.createElement("button");
  btn.classList.add("btn-gemini-inline");
  btn.textContent = "このままプロンプト生成に反映";
  btn.addEventListener("click", () => {
    if (finalPromptEl) {
      finalPromptEl.value = promptText;
      finalPromptEl.focus();
    }
  });

  block.appendChild(label);
  block.appendChild(promptDiv);
  block.appendChild(btn);

  row.appendChild(avatar);
  row.appendChild(block);
  chatWindow.appendChild(row);

  chatWindow.scrollTop = chatWindow.scrollHeight;
}

// 初期メッセージ
function initChat() {
  if (!chatWindow) return;
  chatWindow.innerHTML = "";
  appendMessage(
    "assistant",
    "こんにちは！どんなアイコンを作成したいですか？スタイル、色、テーマなどを教えてください。"
  );
}

initChat();


// チャット送信処理
async function handleSendChat() {
  const message = (chatInput?.value || "").trim();
  if (!message) return;

  appendMessage("user", message);
  if (chatInput) chatInput.value = "";

  try {
    const res = await fetch("/api/gemini/prompt", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ message }),
    });

    if (!res.ok) {
      appendMessage(
        "assistant",
        "うまく生成できませんでした。（サーバーエラー）条件を少し変えてもう一度試してください。"
      );
      return;
    }

    const data = await res.json();
    const prompt = (data && data.prompt) || "";
    if (!prompt) {
      appendMessage(
        "assistant",
        "うまく生成できませんでした。（空の応答）少し内容を変えてもう一度教えてください。"
      );
      return;
    }

    appendPromptSuggestion(prompt);
  } catch (e) {
    console.error(e);
    appendMessage(
      "assistant",
      "うまく生成できませんでした。（ネットワークエラー）時間をおいて再度お試しください。"
    );
  }
}

if (chatSendBtn) {
  chatSendBtn.addEventListener("click", handleSendChat);
}

// Enter は改行、送信しない（Ctrl+Enter で送信したいならここで実装してもOK）
if (chatInput) {
  chatInput.addEventListener("keydown", (e) => {
    // 送信したくないので何もしない
    // 将来 Ctrl+Enter 送信入れる場合はここに条件追加
  });
}


// プロンプトリセット
if (resetPromptBtn && finalPromptEl) {
  resetPromptBtn.addEventListener("click", () => {
    finalPromptEl.value = "";
    finalPromptEl.placeholder =
      "A modern minimalist blue AI assistant icon...";
  });
}


// アイコン生成処理
function renderIcons(icons) {
  if (!iconsGrid) return;
  iconsGrid.innerHTML = "";

  if (!icons || icons.length === 0) {
    const div = document.createElement("div");
    div.className =
      "col-span-2 px-3 py-3 rounded-xl bg-slate-950/60 border border-slate-900 text-[9px] text-slate-500";
    div.innerHTML = `
      <p class="text-slate-300 font-medium mb-1">画像が取得できませんでした。</p>
      <p>条件を少し変えて、もう一度「アイコンを生成」を試してみてください。</p>
    `;
    iconsGrid.appendChild(div);
    return;
  }

  icons.forEach((icon) => {
    const card = document.createElement("div");
    card.classList.add("icon-card");

    const img = document.createElement("img");
    img.src = icon.imageUrl;
    img.alt = "Generated icon";

    card.appendChild(img);

    card.addEventListener("click", () => {
      document
        .querySelectorAll(".icon-card")
        .forEach((c) => c.classList.remove("icon-selected"));
      card.classList.add("icon-selected");
      selectedIconUrl = icon.imageUrl;
      if (downloadBtn) {
        downloadBtn.disabled = false;
      }
    });

    iconsGrid.appendChild(card);
  });
}

async function handleGenerateIcons() {
  if (errorMsg) errorMsg.textContent = "";
  if (downloadBtn) {
    downloadBtn.disabled = true;
  }
  selectedIconUrl = null;

  const prompt = (finalPromptEl?.value || "").trim();
  const size = sizeSelect?.value || "512x512";
  const style = styleSelect?.value || "";
  const count = 4;

  if (!prompt) {
    if (errorMsg) {
      errorMsg.textContent =
        "まず左のチャットでプロンプト案を作成し、「このままプロンプト生成」を押してから実行してください。";
    }
    return;
  }

  // ローディング風スケルトン表示
  if (iconsGrid) {
    iconsGrid.innerHTML = "";
    for (let i = 0; i < count; i++) {
      const sk = document.createElement("div");
      sk.className = "icon-skeleton";
      iconsGrid.appendChild(sk);
    }
  }

  try {
    const res = await fetch("/api/icons/generate", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ prompt, size, style, count }),
    });

    if (!res.ok) {
      if (errorMsg) {
        errorMsg.textContent =
          "画像生成APIの呼び出しに失敗しました。（HTTP " + res.status + "）";
      }
      if (iconsGrid) iconsGrid.innerHTML = "";
      return;
    }

    const data = await res.json();
    if (data.error && errorMsg) {
      errorMsg.textContent = data.error;
    }

    renderIcons(data.icons || []);
  } catch (e) {
    console.error(e);
    if (errorMsg) {
      errorMsg.textContent =
        "画像生成中にエラーが発生しました。ネットワーク状態やAPIキーを確認してください。";
    }
    if (iconsGrid) iconsGrid.innerHTML = "";
  }
}

if (generateBtn) {
  generateBtn.addEventListener("click", handleGenerateIcons);
}


// ダウンロード処理
if (downloadBtn) {
  downloadBtn.addEventListener("click", async () => {
    if (!selectedIconUrl) return;

    try {
      const resp = await fetch(selectedIconUrl);
      const blob = await resp.blob();
      const url = URL.createObjectURL(blob);

      const a = document.createElement("a");
      a.href = url;
      a.download = "aicon.png";
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    } catch (e) {
      console.error(e);
      if (errorMsg) {
        errorMsg.textContent =
          "ダウンロードに失敗しました。画像を右クリックで保存することもできます。";
      }
    }
  });
}