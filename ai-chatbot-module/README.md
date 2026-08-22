# 🤖 DayBot AI Chatbot Integration Guide

This directory contains the **pluggable AI chatbot module** for the Dayflow HRMS. It is designed to be easily dropped into a **React/Vite** frontend and **Node.js/Express** backend.

---

## 📂 Module Structure

```
ai-chatbot-module/
├── backend/
│   ├── geminiService.js   # Core AI service linking to Google Gemini SDK
│   └── chatbotRoute.js    # Express Router exposing chatbot API endpoints
└── frontend/
    ├── DayBotChat.jsx     # Pluggable stateful React chatbot component
    └── DayBotChat.css     # Premium dark-theme glassmorphism styles
```

---

## 🛠️ Step 1: Backend Setup (Node.js/Express)

### 1. Install Dependencies
Run this in your backend project directory:
```bash
npm install @google/generative-ai dotenv express cors
```

### 2. Configure Environment Variables
Add your Gemini API Key to your backend's `.env` file (Get one free at [Google AI Studio](https://aistudio.google.com/apikey)):
```env
GEMINI_API_KEY=your_google_gemini_api_key_here
```

### 3. Integrate Router in `server.js` (or your entry point)
Import and mount the chatbot route in your main server file:

```javascript
const express = require('express');
const app = express();

// 1. Parse JSON body payload
app.use(express.json());

// 2. Import the chatbot router
const chatbotRouter = require('./ai-chatbot-module/backend/chatbotRoute');

// 3. Mount the routes
app.use('/api', chatbotRouter);

// Start server
app.listen(3000, () => console.log('Server running on port 3000'));
```

### 4. Feeding Live Database Context (Recommended)
By default, the AI utilizes built-in HR policy FAQs and local rules. To feed real-time database context (such as active employee lists, leave applications, or holiday calendars):
1. In your frontend, query your database endpoints first.
2. Pass that dataset as a string formatted context into the `<DayBotChat customContext={yourDataString} />` React prop.
3. The backend router will forward this context to Gemini so it has 100% accurate live info.

---

## 💻 Step 2: Frontend Setup (React/Vite)

### 1. Copy Files
Ensure the `DayBotChat.jsx` and `DayBotChat.css` files are placed inside your components directory (e.g. `src/components/DayBotChat/`).

### 2. Import and Mount the Component
Import the chatbot component and drop it at the bottom of your Employee or Admin dashboard pages.

#### For Employee Dashboard:
```jsx
import React from 'react';
import DayBotChat from './components/DayBotChat/DayBotChat';

export default function EmployeeDashboard() {
  // Get active session user info
  const currentUser = { id: "EMP001", name: "Arun Kumar" };

  return (
    <div className="dashboard-container">
      {/* Your main dashboard layout content here */}
      
      {/* Mount Chatbot */}
      <DayBotChat 
        role="employee" 
        employeeId={currentUser.id} 
        apiUrl="http://localhost:3000" // Backend server URL
      />
    </div>
  );
}
```

#### For HR / Admin Dashboard:
```jsx
import React from 'react';
import DayBotChat from './components/DayBotChat/DayBotChat';

export default function AdminDashboard() {
  // Pull database summaries (headcounts, status, leaves) to feed to the chatbot
  const statsContext = `
    Total Employees: 15
    Present Today: 12
    On Leave: 2
    Pending Leave Requests: EMP004 (Sick Leave - 2 days), EMP008 (Casual Leave - 1 day)
  `;

  return (
    <div className="admin-container">
      {/* Your admin management layouts here */}
      
      {/* Mount Chatbot with Admin mode and custom real-time stats context */}
      <DayBotChat 
        role="admin" 
        apiUrl="http://localhost:3000"
        customContext={statsContext}
      />
    </div>
  );
}
```

---

## ⚙️ How it works under the hood
1. **Dynamic Suggestion Chips**: Offers contextual prompts based on the active role (`role="employee"` gets leave balance, salary, and FAQ prompts; `role="admin"` gets reports and roster checks).
2. **Contextual Intelligence**:
   - The **Employee Chatbot** is configured to be friendly, supportive, and strictly restricted to the user's *own* info.
   - The **Admin Chatbot** acts as an operations assistant, presenting clean tabular analytics summaries and department breakdowns.
3. **Session Memory**: Uses a lightweight memory mapping based on `sessionId` inside `geminiService.js` to ensure natural conversational flow.
