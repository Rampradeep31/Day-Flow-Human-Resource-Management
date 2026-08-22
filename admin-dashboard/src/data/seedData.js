// Seed data for Dayflow HRMS

export const initialEmployees = [
  {
    id: "EMP-1001",
    name: "Sarah Connor",
    email: "sarah.connor@dayflow.io",
    password: "Password@123",
    role: "admin", // "admin" | "employee"
    designation: "VP of People Operations",
    department: "Human Resources",
    avatar: "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=300",
    phone: "+1 (555) 234-8901",
    dob: "1988-05-14",
    gender: "Female",
    address: "452 Silicon Boulevard, Suite 800, San Francisco, CA 94107",
    emergencyContact: {
      name: "John Connor",
      relation: "Brother",
      phone: "+1 (555) 890-1234"
    },
    joiningDate: "2021-03-15",
    employmentType: "Full-time",
    workLocation: "San Francisco HQ (Hybrid)",
    reportingManager: "Elena Rostova (Chief Operating Officer)",
    status: "Active",
    salaryStructure: {
      basic: 9500,
      hra: 3800,
      specialAllowance: 2200,
      bonus: 1500,
      providentFund: 1140,
      taxDeduction: 2100,
      insurance: 260
    },
    leaveBalances: {
      paid: { total: 20, used: 4, available: 16 },
      sick: { total: 10, used: 2, available: 8 },
      casual: { total: 8, used: 1, available: 7 },
      unpaid: { total: 0, used: 0, available: 0 }
    },
    documents: [
      { id: "DOC-01", name: "Employment_Agreement_Signed.pdf", type: "Contract", size: "2.4 MB", date: "2021-03-15" },
      { id: "DOC-02", name: "Government_ID_Verification.pdf", type: "ID Proof", size: "1.1 MB", date: "2021-03-10" },
      { id: "DOC-03", name: "Executive_Compensation_Plan.pdf", type: "Salary", size: "850 KB", date: "2024-01-01" }
    ],
    bio: "Passionate People & Talent leader with 12+ years of experience scaling tech workforces with empathy, culture, and operational excellence."
  },
  {
    id: "EMP-1002",
    name: "Alex Rivera",
    email: "alex.rivera@dayflow.io",
    password: "Password@123",
    role: "employee",
    designation: "Lead Full Stack Engineer",
    department: "Engineering",
    avatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300",
    phone: "+1 (555) 789-4321",
    dob: "1994-09-22",
    gender: "Non-binary",
    address: "782 Mission Street, Apt 4B, San Francisco, CA 94103",
    emergencyContact: {
      name: "Carlos Rivera",
      relation: "Father",
      phone: "+1 (555) 345-9876"
    },
    joiningDate: "2022-06-01",
    employmentType: "Full-time",
    workLocation: "Remote (San Francisco Timezone)",
    reportingManager: "Marcus Aurelius (VP of Engineering)",
    status: "Active",
    salaryStructure: {
      basic: 8200,
      hra: 3200,
      specialAllowance: 1800,
      bonus: 1000,
      providentFund: 980,
      taxDeduction: 1750,
      insurance: 220
    },
    leaveBalances: {
      paid: { total: 18, used: 5, available: 13 },
      sick: { total: 10, used: 1, available: 9 },
      casual: { total: 6, used: 2, available: 4 },
      unpaid: { total: 0, used: 0, available: 0 }
    },
    documents: [
      { id: "DOC-11", name: "Software_Engineer_Offer_Letter.pdf", type: "Offer", size: "1.8 MB", date: "2022-05-20" },
      { id: "DOC-12", name: "Passport_Scan_Copy.pdf", type: "ID Proof", size: "3.2 MB", date: "2022-05-25" },
      { id: "DOC-13", name: "Form_W4_Tax_Withholding.pdf", type: "Tax Form", size: "620 KB", date: "2024-01-15" }
    ],
    bio: "Full stack craftsman obsessed with reactive user interfaces, distributed microservices, and slick developer experiences."
  },
  {
    id: "EMP-1003",
    name: "Maya Lin",
    email: "maya.lin@dayflow.io",
    password: "Password@123",
    role: "employee",
    designation: "Senior Product Designer",
    department: "Product Design",
    avatar: "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=300",
    phone: "+1 (555) 432-1098",
    dob: "1996-03-18",
    gender: "Female",
    address: "124 Pine Tree Lane, Oakland, CA 94612",
    emergencyContact: {
      name: "Kenji Lin",
      relation: "Spouse",
      phone: "+1 (555) 678-2345"
    },
    joiningDate: "2023-01-10",
    employmentType: "Full-time",
    workLocation: "San Francisco HQ (In-Office)",
    reportingManager: "Sarah Connor (VP of People Operations)",
    status: "Active",
    salaryStructure: {
      basic: 7600,
      hra: 3000,
      specialAllowance: 1600,
      bonus: 900,
      providentFund: 910,
      taxDeduction: 1580,
      insurance: 200
    },
    leaveBalances: {
      paid: { total: 18, used: 6, available: 12 },
      sick: { total: 10, used: 3, available: 7 },
      casual: { total: 6, used: 1, available: 5 },
      unpaid: { total: 0, used: 0, available: 0 }
    },
    documents: [
      { id: "DOC-21", name: "Design_Lead_Contract.pdf", type: "Contract", size: "2.1 MB", date: "2023-01-05" },
      { id: "DOC-22", name: "Design_System_IP_Assignment.pdf", type: "Legal", size: "900 KB", date: "2023-01-08" }
    ],
    bio: "Crafting intuitive, accessible, and delight-driven design systems. Bridging user research into sleek pixel-perfect digital experiences."
  },
  {
    id: "EMP-1004",
    name: "David Chen",
    email: "david.chen@dayflow.io",
    password: "Password@123",
    role: "employee",
    designation: "Growth Marketing Manager",
    department: "Marketing",
    avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300",
    phone: "+1 (555) 321-9876",
    dob: "1992-11-04",
    gender: "Male",
    address: "904 Market Street, San Francisco, CA 94102",
    emergencyContact: {
      name: "Lisa Chen",
      relation: "Sister",
      phone: "+1 (555) 456-7890"
    },
    joiningDate: "2022-09-15",
    employmentType: "Full-time",
    workLocation: "San Francisco HQ (Hybrid)",
    reportingManager: "Sarah Connor (VP of People Operations)",
    status: "Active",
    salaryStructure: {
      basic: 7100,
      hra: 2800,
      specialAllowance: 1500,
      bonus: 1200,
      providentFund: 850,
      taxDeduction: 1490,
      insurance: 190
    },
    leaveBalances: {
      paid: { total: 18, used: 8, available: 10 },
      sick: { total: 10, used: 2, available: 8 },
      casual: { total: 6, used: 0, available: 6 },
      unpaid: { total: 0, used: 0, available: 0 }
    },
    documents: [
      { id: "DOC-31", name: "Marketing_Manager_Offer.pdf", type: "Offer", size: "1.5 MB", date: "2022-09-01" },
      { id: "DOC-32", name: "NDA_and_Security_Policy.pdf", type: "Compliance", size: "1.2 MB", date: "2022-09-10" }
    ],
    bio: "Data-driven growth marketer passionate about funnel optimization, user acquisition storytelling, and B2B SaaS community building."
  },
  {
    id: "EMP-1005",
    name: "Priya Sharma",
    email: "priya.sharma@dayflow.io",
    password: "Password@123",
    role: "employee",
    designation: "DevOps & Cloud Architect",
    department: "Engineering",
    avatar: "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?auto=format&fit=crop&q=80&w=300",
    phone: "+1 (555) 654-3210",
    dob: "1991-07-30",
    gender: "Female",
    address: "310 Fremont St, San Francisco, CA 94105",
    emergencyContact: {
      name: "Amit Sharma",
      relation: "Spouse",
      phone: "+1 (555) 789-0123"
    },
    joiningDate: "2023-04-01",
    employmentType: "Full-time",
    workLocation: "Remote (Global)",
    reportingManager: "Alex Rivera (Lead Full Stack Engineer)",
    status: "Active",
    salaryStructure: {
      basic: 8400,
      hra: 3300,
      specialAllowance: 1900,
      bonus: 1100,
      providentFund: 1000,
      taxDeduction: 1820,
      insurance: 230
    },
    leaveBalances: {
      paid: { total: 18, used: 3, available: 15 },
      sick: { total: 10, used: 1, available: 9 },
      casual: { total: 6, used: 1, available: 5 },
      unpaid: { total: 0, used: 0, available: 0 }
    },
    documents: [
      { id: "DOC-41", name: "DevOps_Architect_Contract.pdf", type: "Contract", size: "2.3 MB", date: "2023-03-25" },
      { id: "DOC-42", name: "AWS_Cloud_Certifications.pdf", type: "Certifications", size: "4.1 MB", date: "2023-04-01" }
    ],
    bio: "Cloud infrastructure enthusiast. Specializing in Kubernetes, zero-trust network topologies, CI/CD automation, and high availability systems."
  },
  {
    id: "EMP-1006",
    name: "Marcus Vance",
    email: "marcus.vance@dayflow.io",
    password: "Password@123",
    role: "employee",
    designation: "Financial Controller",
    department: "Finance",
    avatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=300",
    phone: "+1 (555) 987-6543",
    dob: "1987-12-11",
    gender: "Male",
    address: "510 Montgomery Street, San Francisco, CA 94111",
    emergencyContact: {
      name: "Hannah Vance",
      relation: "Spouse",
      phone: "+1 (555) 890-4321"
    },
    joiningDate: "2021-11-15",
    employmentType: "Full-time",
    workLocation: "San Francisco HQ (Hybrid)",
    reportingManager: "Sarah Connor (VP of People Operations)",
    status: "Active",
    salaryStructure: {
      basic: 8900,
      hra: 3500,
      specialAllowance: 2000,
      bonus: 1400,
      providentFund: 1070,
      taxDeduction: 1950,
      insurance: 250
    },
    leaveBalances: {
      paid: { total: 20, used: 7, available: 13 },
      sick: { total: 10, used: 2, available: 8 },
      casual: { total: 6, used: 2, available: 4 },
      unpaid: { total: 0, used: 0, available: 0 }
    },
    documents: [
      { id: "DOC-51", name: "CPA_License_Verification.pdf", type: "Certification", size: "1.4 MB", date: "2021-11-10" },
      { id: "DOC-52", name: "Corporate_Ethics_Acknowledgment.pdf", type: "Compliance", size: "890 KB", date: "2021-11-15" }
    ],
    bio: "Overseeing financial reporting, corporate governance, payroll fidelity, and strategic resource allocation with precision."
  }
];

export const initialLeaveRequests = [
  {
    id: "LR-9021",
    employeeId: "EMP-1002",
    employeeName: "Alex Rivera",
    employeeAvatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300",
    department: "Engineering",
    type: "Paid", // Paid | Sick | Unpaid | Casual
    startDate: "2026-08-28",
    endDate: "2026-08-30",
    daysCount: 3,
    halfDay: null,
    reason: "Attending annual family reunion and taking scheduled personal downtime.",
    attachment: "Flight_Booking_Confirmation.pdf",
    status: "Pending", // Pending | Approved | Rejected
    appliedOn: "2026-08-20",
    adminComment: "",
    reviewedBy: null,
    reviewedOn: null
  },
  {
    id: "LR-9022",
    employeeId: "EMP-1003",
    employeeName: "Maya Lin",
    employeeAvatar: "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=300",
    department: "Product Design",
    type: "Sick",
    startDate: "2026-08-24",
    endDate: "2026-08-24",
    daysCount: 1,
    halfDay: "First Half",
    reason: "Medical consultation & dental surgery checkup.",
    attachment: "Doctor_Appointment_Slip.pdf",
    status: "Approved",
    appliedOn: "2026-08-19",
    adminComment: "Approved. Hope all goes well with the dental appointment!",
    reviewedBy: "Sarah Connor",
    reviewedOn: "2026-08-19"
  },
  {
    id: "LR-9023",
    employeeId: "EMP-1004",
    employeeName: "David Chen",
    employeeAvatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300",
    department: "Marketing",
    type: "Paid",
    startDate: "2026-09-02",
    endDate: "2026-09-08",
    daysCount: 5,
    halfDay: null,
    reason: "Annual vacation trip to national parks.",
    attachment: null,
    status: "Pending",
    appliedOn: "2026-08-21",
    adminComment: "",
    reviewedBy: null,
    reviewedOn: null
  },
  {
    id: "LR-9024",
    employeeId: "EMP-1005",
    employeeName: "Priya Sharma",
    employeeAvatar: "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?auto=format&fit=crop&q=80&w=300",
    department: "Engineering",
    type: "Casual",
    startDate: "2026-08-15",
    endDate: "2026-08-15",
    daysCount: 1,
    halfDay: null,
    reason: "Personal home maintenance and relocation deliveries.",
    attachment: null,
    status: "Approved",
    appliedOn: "2026-08-10",
    adminComment: "Enjoy the settled move!",
    reviewedBy: "Sarah Connor",
    reviewedOn: "2026-08-11"
  },
  {
    id: "LR-9025",
    employeeId: "EMP-1006",
    employeeName: "Marcus Vance",
    employeeAvatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=300",
    department: "Finance",
    type: "Unpaid",
    startDate: "2026-07-10",
    endDate: "2026-07-14",
    daysCount: 3,
    halfDay: null,
    reason: "Personal sabbatical days requested beyond allocated quota.",
    attachment: null,
    status: "Approved",
    appliedOn: "2026-07-01",
    adminComment: "Approved as unpaid sabbatical.",
    reviewedBy: "Sarah Connor",
    reviewedOn: "2026-07-02"
  },
  {
    id: "LR-9026",
    employeeId: "EMP-1002",
    employeeName: "Alex Rivera",
    employeeAvatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300",
    department: "Engineering",
    type: "Paid",
    startDate: "2026-06-18",
    endDate: "2026-06-20",
    daysCount: 3,
    halfDay: null,
    reason: "Attending React & AI Developer Summit in Austin.",
    attachment: "Conference_Pass.pdf",
    status: "Approved",
    appliedOn: "2026-06-05",
    adminComment: "Have a great learning experience!",
    reviewedBy: "Sarah Connor",
    reviewedOn: "2026-06-06"
  }
];

export const initialAttendanceRecords = [
  // August 2026 logs
  {
    id: "ATT-2026-08-22-1002",
    employeeId: "EMP-1002",
    employeeName: "Alex Rivera",
    date: "2026-08-22",
    checkIn: "09:04 AM",
    checkOut: null, // Currently checked in!
    workMode: "Remote",
    status: "Present", // Present | Absent | Half-day | Leave | Holiday
    workHours: "2.6 hrs",
    breakMinutes: 15,
    notes: "Sprint review prep & code reviews"
  },
  {
    id: "ATT-2026-08-22-1001",
    employeeId: "EMP-1001",
    employeeName: "Sarah Connor",
    date: "2026-08-22",
    checkIn: "08:45 AM",
    checkOut: null,
    workMode: "Office",
    status: "Present",
    workHours: "2.9 hrs",
    breakMinutes: 0,
    notes: "Executive 1:1s and HR operations"
  },
  {
    id: "ATT-2026-08-22-1003",
    employeeId: "EMP-1003",
    employeeName: "Maya Lin",
    date: "2026-08-22",
    checkIn: "09:15 AM",
    checkOut: null,
    workMode: "Office",
    status: "Present",
    workHours: "2.4 hrs",
    breakMinutes: 0,
    notes: "Dayflow v2 UI design audit"
  },
  {
    id: "ATT-2026-08-22-1004",
    employeeId: "EMP-1004",
    employeeName: "David Chen",
    date: "2026-08-22",
    checkIn: null,
    checkOut: null,
    workMode: "Remote",
    status: "Absent",
    workHours: "0.0 hrs",
    breakMinutes: 0,
    notes: "Unnotified absence"
  },
  {
    id: "ATT-2026-08-22-1005",
    employeeId: "EMP-1005",
    employeeName: "Priya Sharma",
    date: "2026-08-22",
    checkIn: "08:30 AM",
    checkOut: null,
    workMode: "Remote",
    status: "Present",
    workHours: "3.1 hrs",
    breakMinutes: 10,
    notes: "Infrastructure migration"
  },
  {
    id: "ATT-2026-08-22-1006",
    employeeId: "EMP-1006",
    employeeName: "Marcus Vance",
    date: "2026-08-22",
    checkIn: "09:30 AM",
    checkOut: null,
    workMode: "Office",
    status: "Present",
    workHours: "2.1 hrs",
    breakMinutes: 0,
    notes: "Month-end ledger audit"
  },
  // Previous Days of August for Alex Rivera (EMP-1002)
  {
    id: "ATT-2026-08-21-1002",
    employeeId: "EMP-1002",
    employeeName: "Alex Rivera",
    date: "2026-08-21",
    checkIn: "09:00 AM",
    checkOut: "05:45 PM",
    workMode: "Remote",
    status: "Present",
    workHours: "8.5 hrs",
    breakMinutes: 45,
    notes: "Full day core backend API development"
  },
  {
    id: "ATT-2026-08-20-1002",
    employeeId: "EMP-1002",
    employeeName: "Alex Rivera",
    date: "2026-08-20",
    checkIn: "08:55 AM",
    checkOut: "06:10 PM",
    workMode: "Office",
    status: "Present",
    workHours: "8.8 hrs",
    breakMinutes: 50,
    notes: "On-site planning and pairing session"
  },
  {
    id: "ATT-2026-08-19-1002",
    employeeId: "EMP-1002",
    employeeName: "Alex Rivera",
    date: "2026-08-19",
    checkIn: "09:10 AM",
    checkOut: "01:30 PM",
    workMode: "Remote",
    status: "Half-day",
    workHours: "4.3 hrs",
    breakMinutes: 20,
    notes: "Half day due to power maintenance"
  },
  {
    id: "ATT-2026-08-18-1002",
    employeeId: "EMP-1002",
    employeeName: "Alex Rivera",
    date: "2026-08-18",
    checkIn: "09:05 AM",
    checkOut: "05:50 PM",
    workMode: "Remote",
    status: "Present",
    workHours: "8.4 hrs",
    breakMinutes: 40,
    notes: "Security patch release"
  },
  {
    id: "ATT-2026-08-17-1002",
    employeeId: "EMP-1002",
    employeeName: "Alex Rivera",
    date: "2026-08-17",
    checkIn: "08:48 AM",
    checkOut: "05:30 PM",
    workMode: "Office",
    status: "Present",
    workHours: "8.2 hrs",
    breakMinutes: 45,
    notes: "Sprint kickoff and architectural spikes"
  },
  {
    id: "ATT-2026-08-16-1002",
    employeeId: "EMP-1002",
    employeeName: "Alex Rivera",
    date: "2026-08-16",
    checkIn: null,
    checkOut: null,
    workMode: null,
    status: "Holiday",
    workHours: "0.0 hrs",
    breakMinutes: 0,
    notes: "Sunday Weekend"
  },
  {
    id: "ATT-2026-08-15-1002",
    employeeId: "EMP-1002",
    employeeName: "Alex Rivera",
    date: "2026-08-15",
    checkIn: null,
    checkOut: null,
    workMode: null,
    status: "Holiday",
    workHours: "0.0 hrs",
    breakMinutes: 0,
    notes: "National Holiday - Independence Celebrations"
  },
  {
    id: "ATT-2026-08-14-1002",
    employeeId: "EMP-1002",
    employeeName: "Alex Rivera",
    date: "2026-08-14",
    checkIn: null,
    checkOut: null,
    workMode: null,
    status: "Leave",
    workHours: "0.0 hrs",
    breakMinutes: 0,
    notes: "Approved Sick Leave"
  }
];

export const companyHolidays = [
  { date: "2026-08-15", name: "Summer Freedom Gala", type: "Company Holiday" },
  { date: "2026-09-07", name: "Labor Day", type: "National Holiday" },
  { date: "2026-11-26", name: "Thanksgiving Day", type: "National Holiday" },
  { date: "2026-12-25", name: "Winter Break & Christmas", type: "Company Holiday" },
  { date: "2027-01-01", name: "New Year's Day", type: "National Holiday" }
];

export const companyAnnouncements = [
  {
    id: "ANN-101",
    title: "Dayflow HRMS v2.4 Platform Launch 🎉",
    category: "System Update",
    author: "Sarah Connor (HR Admin)",
    date: "2026-08-20",
    content: "Welcome to our upgraded Dayflow HRMS portal! Enjoy real-time workday tracking, instant leave approvals, transparent salary structure breakdowns, and dark mode themes.",
    pinned: true
  },
  {
    id: "ANN-102",
    title: "Annual Health & Wellness Benefit Enrollment",
    category: "Benefits",
    author: "People Operations",
    date: "2026-08-18",
    content: "Open enrollment for our comprehensive dental, vision, and wellness gym stipends is now live. Review your document portal for plan guides.",
    pinned: false
  },
  {
    id: "ANN-103",
    title: "Engineering Hackathon: Q3 AI Automation",
    category: "Event",
    author: "Alex Rivera (Engineering)",
    date: "2026-08-15",
    content: "Sign up for next month's 48-hour internal hackathon. Prizes include tech stipends and prototype integration into core products.",
    pinned: false
  }
];

export const initialNotifications = [
  {
    id: "NOTIF-01",
    title: "Leave Request Submitted",
    message: "Alex Rivera applied for 3 days Paid Leave (Aug 28 - Aug 30).",
    type: "leave",
    timestamp: "2 hours ago",
    read: false,
    targetRole: "admin",
    linkTab: "leaves"
  },
  {
    id: "NOTIF-02",
    title: "Leave Approved!",
    message: "Your Sick Leave request for Aug 24 has been approved by Sarah Connor.",
    type: "approval",
    timestamp: "1 day ago",
    read: false,
    targetRole: "employee",
    linkTab: "leaves"
  },
  {
    id: "NOTIF-03",
    title: "July 2026 Payslip Available",
    message: "Your salary slip for July 2026 has been generated and is ready for download.",
    type: "payroll",
    timestamp: "3 days ago",
    read: true,
    targetRole: "all",
    linkTab: "payroll"
  },
  {
    id: "NOTIF-04",
    title: "Punctuality Badge Earned 🌟",
    message: "You maintained a 98% on-time check-in record for the past 30 workdays!",
    type: "system",
    timestamp: "5 days ago",
    read: true,
    targetRole: "employee",
    linkTab: "attendance"
  }
];

export const historicalPayslips = [
  {
    month: "July 2026",
    payPeriod: "July 1, 2026 - July 31, 2026",
    paymentDate: "July 31, 2026",
    status: "Paid",
    bankRef: "ACH-98214490"
  },
  {
    month: "June 2026",
    payPeriod: "June 1, 2026 - June 30, 2026",
    paymentDate: "June 30, 2026",
    status: "Paid",
    bankRef: "ACH-87201948"
  },
  {
    month: "May 2026",
    payPeriod: "May 1, 2026 - May 31, 2026",
    paymentDate: "May 31, 2026",
    status: "Paid",
    bankRef: "ACH-76340212"
  },
  {
    month: "April 2026",
    payPeriod: "April 1, 2026 - April 30, 2026",
    paymentDate: "April 30, 2026",
    status: "Paid",
    bankRef: "ACH-65129840"
  }
];
