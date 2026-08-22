// ============================================================
// DayBot — Gemini AI Service (Pluggable Module)
// ============================================================

const { GoogleGenerativeAI } = require("@google/generative-ai");

let genAI = null;

/**
 * Initializes the Gemini Generative AI client.
 * @param {string} apiKey - The Google Gemini API key.
 */
function initGemini(apiKey) {
  if (apiKey && apiKey !== "your_api_key_here") {
    genAI = new GoogleGenerativeAI(apiKey);
    console.log("✅ DayBot AI: Gemini client initialized successfully");
  } else {
    console.warn("⚠️ DayBot AI: Missing or invalid GEMINI_API_KEY. Running in fallback mode.");
  }
}

// Conversation stores (in-memory, mapping sessionId -> chat history array)
const conversations = new Map();

/**
 * Sends a message to the Gemini API with context and role rules.
 * Supports fallback answers if no API key is set.
 * 
 * @param {object} params - Input parameters
 * @param {string} params.role - User role: "employee" or "admin"
 * @param {string} params.message - The prompt message from the user
 * @param {string} params.sessionId - Unique session identifier
 * @param {string} [params.employeeId] - ID of the employee (required for employee role context)
 * @param {string} [params.customContext] - Dynamic database context (policies, stats, rosters) provided by parent app
 */
async function chat({ role, message, sessionId, employeeId, customContext }) {
  if (!genAI) {
    return getFallbackResponse(role, message, employeeId, customContext);
  }

  try {
    const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash" });

    // Build system instructions
    const systemPrompt = role === "employee" 
      ? getEmployeeSystemPrompt(employeeId) 
      : getAdminSystemPrompt();

    const fullInstruction = `${systemPrompt}

--- SYSTEM DATA CONTEXT ---
${customContext || "No dynamic system data context provided."}
---

Respond to the user's message based on the guidelines and context provided above. Always keep formatting clean and use Markdown (bold, lists, etc.) appropriately.`;

    // Retrieve or initialize conversation history
    if (!conversations.has(sessionId)) {
      conversations.set(sessionId, []);
    }
    const history = conversations.get(sessionId);

    // Start chat session with Gemini
    const chatSession = model.startChat({
      history: history.map(h => ({
        role: h.role,
        parts: [{ text: h.text }]
      })),
      systemInstruction: fullInstruction,
    });

    const result = await chatSession.sendMessage(message);
    const responseText = result.response.text();

    // Save to conversation history
    history.push({ role: "user", text: message });
    history.push({ role: "model", text: responseText });

    // Limit conversation history size to last 20 messages to keep context window light
    if (history.length > 20) {
      history.splice(0, history.length - 20);
    }

    return responseText;
  } catch (error) {
    console.error("❌ DayBot AI Gemini Error:", error.message);
    return `⚠️ I'm having trouble connecting to my AI brain right now. Here is a helper response based on local rules:\n\n${getFallbackResponse(role, message, employeeId, customContext)}`;
  }
}

/**
 * Clears the session history.
 * @param {string} sessionId 
 */
function clearConversation(sessionId) {
  conversations.delete(sessionId);
}

// ─── System Instructions Builder ────────────────────────────

function getEmployeeSystemPrompt(employeeId) {
  return `You are "DayBot", the AI HR Assistant for Dayflow Technologies. You are chatting with an EMPLOYEE (ID: ${employeeId || "unknown"}).

YOUR PERSONALITY:
- Friendly, warm, helpful, and professional.
- Use bullet points, bold headers, and emojis (👋, 📋, 💰, 🗓️) to keep responses readable.
- Keep answers concise and direct.

YOUR CAPABILITIES:
- Explain HR policies (leave allowances, attendance rules, dress codes, onboarding FAQs).
- Assist the employee with their own profile, leave balance, attendance records, or salary structure if available in the context.
- Guide them on how to perform actions (e.g. how to apply for leaves, edit profile, download slips).

CRITICAL RULES:
- Never share details about OTHER employees. You are only allowed to discuss data relating to the current employee (ID: ${employeeId}).
- Do not make up information. If policies or details are not provided in the context, politely suggest contacting HR (sneha.reddy@dayflow.com).
- Direct the user to the appropriate UI pages if they want to perform transactional operations (like checking-in or submitting a leave request).`;
}

function getAdminSystemPrompt() {
  return `You are "DayBot", the AI HR Assistant for Dayflow Technologies. You are chatting with an HR ADMINISTRATOR or Manager.

YOUR PERSONALITY:
- Highly professional, clear, efficient, and data-driven.
- Use structured formats (like tables, bullet points, lists).
- Keep formatting clean and professional.

YOUR CAPABILITIES:
- Provide workforce overview statistics (attendance rates, headcount, active leaves).
- Help filter, list, or search the employee roster provided in the context.
- Highlight pending items (e.g. leave requests waiting for approval).
- Answer policy guidelines questions.

CRITICAL RULES:
- You have administrative authorization to view all employee details provided in the system data context.
- Help summarize data quickly to assist admin decisions.
- Do not perform transactions directly (e.g. do not approve leaves in conversation). Advise the admin to click the Approve/Reject buttons in the dashboard interface.`;
}

// ─── Rule-Based Fallback Handler ─────────────────────────────

function getFallbackResponse(role, message, employeeId, context) {
  const msg = message.toLowerCase();
  
  if (msg.includes("leave policy") || msg.includes("how many leaves")) {
    return `📋 **Dayflow Leave Policy:**
- **Casual Leave (CL):** 12 days/year. Max 3 consecutive days.
- **Sick Leave (SL):** 7 days/year.
- **Earned Leave (EL):** 15 days/year.
- *How to Apply:* Navigate to the **Leave Management** page, fill in the dates and reason, and submit for manager approval.`;
  }

  if (msg.includes("attendance") && (msg.includes("rule") || msg.includes("policy") || msg.includes("hours"))) {
    return `🕒 **Dayflow Attendance Policy:**
- **Work Hours:** 9:00 AM to 6:00 PM (9 hours, including 1 hour lunch).
- **Grace Period:** 15 minutes (check-in by 9:15 AM).
- **Missed Check-ins:** Please contact HR or your manager for a manual regularization request.`;
  }

  if (msg.includes("hello") || msg.includes("hi") || msg.includes("hey")) {
    return `👋 Hello! I'm **DayBot**, your HR assistant chatbot.
    
I can answer questions about:
- 📋 HR Policies & Leave Rules
- 🗓️ Company Holidays
- 💰 Salary Slip structures
- ❓ Common Onboarding FAQs

What can I help you with today?`;
  }

  return `I'm currently running in offline backup mode, but I can help you with leave policies, attendance rules, and holiday queries! Try asking:
- *"What is the leave policy?"*
- *"Show attendance rules"*
- Or contact HR directly at **hr@dayflow.com** for assistance.`;
}

module.exports = {
  initGemini,
  chat,
  clearConversation
};
