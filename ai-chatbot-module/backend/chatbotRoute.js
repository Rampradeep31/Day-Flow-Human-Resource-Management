// ============================================================
// DayBot — Express Router for Chatbot Endpoints
// ============================================================

const express = require("express");
const router = express.Router();
const { chat, clearConversation } = require("./geminiService");

/**
 * @route   POST /chat/employee
 * @desc    Get chatbot response for the Employee role
 * @access  Private (Depends on parent app authentication)
 * @body    { string message, string sessionId, string employeeId, string [customContext] }
 */
router.post("/chat/employee", async (req, res) => {
  try {
    const { message, sessionId, employeeId, customContext } = req.body;

    if (!message) {
      return res.status(400).json({ error: "Message field is required." });
    }
    if (!sessionId) {
      return res.status(400).json({ error: "SessionId field is required for tracking conversation history." });
    }

    const aiResponse = await chat({
      role: "employee",
      message,
      sessionId,
      employeeId: employeeId || "unknown",
      customContext
    });

    res.json({
      success: true,
      role: "employee",
      response: aiResponse
    });
  } catch (error) {
    console.error("❌ DayBot Router Employee Error:", error);
    res.status(500).json({
      success: false,
      error: "Failed to fetch response from chat assistant."
    });
  }
});

/**
 * @route   POST /chat/admin
 * @desc    Get chatbot response for the HR/Admin role
 * @access  Private (Depends on parent app admin authentication)
 * @body    { string message, string sessionId, string [customContext] }
 */
router.post("/chat/admin", async (req, res) => {
  try {
    const { message, sessionId, customContext } = req.body;

    if (!message) {
      return res.status(400).json({ error: "Message field is required." });
    }
    if (!sessionId) {
      return res.status(400).json({ error: "SessionId field is required for tracking conversation history." });
    }

    const aiResponse = await chat({
      role: "admin",
      message,
      sessionId,
      customContext
    });

    res.json({
      success: true,
      role: "admin",
      response: aiResponse
    });
  } catch (error) {
    console.error("❌ DayBot Router Admin Error:", error);
    res.status(500).json({
      success: false,
      error: "Failed to fetch response from chat assistant."
    });
  }
});

/**
 * @route   POST /chat/clear
 * @desc    Clear conversation history for a given sessionId
 * @access  Private
 * @body    { string sessionId }
 */
router.post("/chat/clear", (req, res) => {
  const { sessionId } = req.body;
  if (!sessionId) {
    return res.status(400).json({ error: "SessionId is required." });
  }

  clearConversation(sessionId);
  res.json({
    success: true,
    message: `Chat history cleared for session: ${sessionId}`
  });
});

module.exports = router;
