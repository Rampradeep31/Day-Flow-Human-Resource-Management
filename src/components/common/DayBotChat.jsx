import React, { useState, useEffect, useRef } from "react";
import { useHRMS } from "../../context/HRMSContext";
import "./DayBotChat.css";

/**
 * DayBotChat — Pluggable React Chatbot Component (Integrated with HRMS Context)
 * 
 * Props:
 * - apiUrl: string (default: "http://localhost:3000")
 */
export default function DayBotChat({ apiUrl = "http://localhost:3000" }) {
  const { 
    currentUser, 
    employees, 
    leaveRequests, 
    attendanceRecords 
  } = useHRMS();

  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [inputValue, setInputValue] = useState("");
  const [isTyping, setIsTyping] = useState(false);
  const [sessionId] = useState(`session-${Date.now()}-${Math.random().toString(36).substr(2, 5)}`);

  const messagesEndRef = useRef(null);

  // Determine role based on currentUser from context
  const role = currentUser?.role === "admin" ? "admin" : "employee";
  const employeeId = currentUser?.id || "EMP-1001";

  const employeeSuggestions = [
    "What is the leave policy?",
    "Show my leave balance",
    "My salary details",
    "How to apply for leave?",
    "Upcoming holidays"
  ];

  const adminSuggestions = [
    "How many on leave today?",
    "Pending leave approvals",
    "Department headcount",
    "Today's attendance",
    "Show all employees"
  ];

  const suggestions = role === "admin" ? adminSuggestions : employeeSuggestions;

  // Add welcome message on initial load/user switch
  useEffect(() => {
    const greeting = role === "admin"
      ? `👋 Hello Admin (${currentUser?.name})! I'm **DayBot**, your AI HR Administrator assistant.\n\nI can help you analyze department headcount, view attendance rates, check pending leave approvals, or answer general policy questions.`
      : `👋 Hello ${currentUser?.name}! I'm **DayBot**, your AI HR assistant.\n\nI can help you check your leave balance, explain the leave policies, view your salary structure, or fetch upcoming holidays.\n\nWhat would you like to ask?`;
    
    setMessages([{ sender: "bot", text: greeting }]);
  }, [role, currentUser]);

  // Scroll to bottom whenever messages list updates
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages, isTyping]);

  const toggleChat = () => setIsOpen(!isOpen);

  // Helper to compile dynamic context from React Context
  const getDynamicContext = () => {
    if (role === "admin") {
      const pendingLeaves = leaveRequests.filter(l => l.status === "Pending");
      const activeEmployees = employees.filter(e => e.status === "Active");
      const presentCount = attendanceRecords.filter(a => a.date === new Date().toISOString().split("T")[0] && a.status === "Present").length;

      // Department counts
      const depts = {};
      employees.forEach(e => depts[e.department] = (depts[e.department] || 0) + 1);

      return `
ADMIN CONTEXT:
Active Employees: ${activeEmployees.length}
Present Today: ${presentCount}
Pending Leaves: ${pendingLeaves.length}
Department counts: ${JSON.stringify(depts)}
Pending leave list: ${JSON.stringify(pendingLeaves.map(l => ({ name: l.employeeName, type: l.type, days: l.days, reason: l.reason })))}
Employee roster: ${JSON.stringify(employees.map(e => ({ name: e.name, id: e.id, department: e.department, designation: e.designation, status: e.status })))}
`;
    } else {
      const myLeaves = leaveRequests.filter(l => l.employeeId === employeeId);
      const myAttendance = attendanceRecords.filter(a => a.employeeId === employeeId);
      
      return `
EMPLOYEE CONTEXT:
Name: ${currentUser.name}
ID: ${currentUser.id}
Email: ${currentUser.email}
Department: ${currentUser.department}
Designation: ${currentUser.designation}
Reporting Manager: ${currentUser.reportingManager || "N/A"}
Leave Balance: ${JSON.stringify(currentUser.leaveBalance || {})}
Leaves Applied: ${JSON.stringify(myLeaves.map(l => ({ type: l.type, days: l.days, status: l.status, startDate: l.startDate, endDate: l.endDate })))}
Attendance log: ${JSON.stringify(myAttendance.map(a => ({ date: a.date, status: a.status })))}
Salary structure: ${JSON.stringify(currentUser.salary || {})}
`;
    }
  };

  const handleSend = async (textToSend) => {
    const text = textToSend || inputValue.trim();
    if (!text) return;

    // Add user message to UI
    setMessages((prev) => [...prev, { sender: "user", text }]);
    setInputValue("");
    setIsTyping(true);

    try {
      const endpoint = role === "admin" ? "/api/chat/admin" : "/api/chat/employee";
      const customContext = getDynamicContext();

      const response = await fetch(`${apiUrl}${endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          message: text,
          sessionId,
          employeeId,
          customContext
        })
      });

      const data = await response.json();
      setIsTyping(false);

      if (data.success && data.response) {
        setMessages((prev) => [...prev, { sender: "bot", text: data.response }]);
      } else {
        setMessages((prev) => [
          ...prev, 
          { sender: "bot", text: "⚠️ Sorry, I encountered an error. Please try again." }
        ]);
      }
    } catch (error) {
      console.error("DayBot Fetch Error:", error);
      setIsTyping(false);
      setMessages((prev) => [
        ...prev, 
        { sender: "bot", text: "⚠️ Connection error. Please ensure the backend server is running." }
      ]);
    }
  };

  // Basic markdown formatter for standard bold, bullet lists, and links
  const formatMarkdown = (text) => {
    if (!text) return "";
    
    // Bold: **text**
    let formatted = text.replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>");
    
    // Unordered lists: - item
    formatted = formatted.replace(/^- (.*)/gm, "<li>$1</li>");
    
    // Wrap <li> blocks in <ul>
    formatted = formatted.replace(/((<li>.*<\/li>\n?)+)/g, "<ul>$1</ul>");
    
    // Line breaks
    formatted = formatted.replace(/\n/g, "<br />");
    
    return <span dangerouslySetInnerHTML={{ __html: formatted }} />;
  };

  return (
    <>
      {/* Floating Action Button */}
      <button 
        className={`daybot-fab ${isOpen ? "open" : ""}`} 
        onClick={toggleChat}
        title="Chat with DayBot AI"
      >
        {isOpen ? "✕" : "🤖"}
      </button>

      {/* Chat Window */}
      <div className={`daybot-window ${isOpen ? "open" : ""}`}>
        {/* Header */}
        <div className="daybot-header">
          <div className="daybot-avatar">🤖</div>
          <div className="daybot-title">
            <h4>DayBot AI</h4>
            <span>{role === "admin" ? "HR Admin Assistant" : "Your HR Assistant"}</span>
          </div>
          <button className="daybot-close" onClick={() => setIsOpen(false)}>✕</button>
        </div>

        {/* Messages Container */}
        <div className="daybot-messages">
          {messages.map((msg, index) => (
            <div key={index} className={`daybot-message ${msg.sender}`}>
              <div className="daybot-msg-avatar">
                {msg.sender === "bot" ? "🤖" : "👤"}
              </div>
              <div className="daybot-msg-bubble">
                {formatMarkdown(msg.text)}
              </div>
            </div>
          ))}

          {/* Typing Indicator */}
          {isTyping && (
            <div className="daybot-message bot">
              <div className="daybot-msg-avatar">🤖</div>
              <div className="daybot-msg-bubble">
                <div className="daybot-typing">
                  <div className="daybot-dot"></div>
                  <div className="daybot-dot"></div>
                  <div className="daybot-dot"></div>
                </div>
              </div>
            </div>
          )}
          
          <div ref={messagesEndRef} />
        </div>

        {/* Suggestion Chips */}
        {messages.length === 1 && !isTyping && (
          <div className="daybot-suggestions">
            {suggestions.map((sug, idx) => (
              <button 
                key={idx} 
                className="daybot-chip" 
                onClick={() => handleSend(sug)}
              >
                {sug}
              </button>
            ))}
          </div>
        )}

        {/* Input Form */}
        <form 
          className="daybot-input-area" 
          onSubmit={(e) => { e.preventDefault(); handleSend(); }}
        >
          <input 
            type="text" 
            placeholder="Ask me anything about HR..." 
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            disabled={isTyping}
          />
          <button 
            type="submit" 
            className="daybot-send-btn" 
            disabled={!inputValue.trim() || isTyping}
          >
            ➤
          </button>
        </form>
      </div>
    </>
  );
}
