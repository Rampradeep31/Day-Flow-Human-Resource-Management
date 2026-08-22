require("dotenv").config();
const express = require("express");
const cors = require("cors");
const chatbotRouter = require("./chatbotRoute");
const { initGemini } = require("./geminiService");

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Initialize Gemini SDK
const apiKey = process.env.GEMINI_API_KEY;
if (apiKey) {
  initGemini(apiKey);
  console.log("✅ Gemini AI Initialized");
} else {
  console.log("⚠️ No GEMINI_API_KEY found in environment. Chatbot will run in offline fallback mode.");
}

// Mount the routes
app.use("/api", chatbotRouter);

app.listen(PORT, () => {
  console.log(`🤖 DayBot Chatbot backend running on http://localhost:${PORT}`);
});
