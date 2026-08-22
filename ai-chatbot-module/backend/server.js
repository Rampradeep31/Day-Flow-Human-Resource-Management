require("dotenv").config();
const express = require("express");
const cors = require("cors");
const chatbotRouter = require("./chatbotRoute");
const { initGemini } = require("./geminiService");

const app = express();
const PORT = process.env.PORT || 3000;
const allowedOrigin = process.env.FRONTEND_URL || "http://localhost:5173";

app.use(cors({
  origin: allowedOrigin,
  methods: ["GET", "POST", "OPTIONS"],
  allowedHeaders: ["Content-Type"],
}));
app.use(express.json({ limit: "256kb" }));

// Initialize Gemini SDK
const apiKey = process.env.GEMINI_API_KEY;
if (apiKey) {
  initGemini(apiKey);
  console.log("✅ Gemini AI Initialized");
} else {
  console.log("⚠️ No GEMINI_API_KEY found in environment. Chatbot will run in offline fallback mode.");
}

// Mount the routes
app.get("/health", (_req, res) => {
  res.json({ status: "UP", service: "dayflow-chatbot" });
});

app.use("/api", chatbotRouter);

app.listen(PORT, () => {
  console.log(`🤖 DayBot Chatbot backend running on http://localhost:${PORT}`);
});
