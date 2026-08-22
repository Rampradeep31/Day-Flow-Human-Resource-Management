import React from "react";
import { Users, CalendarCheck2, Clock, DollarSign, ArrowUpRight } from "lucide-react";

export default function StatCards({ 
  totalEmployees = 0, 
  presentToday = 0, 
  pendingLeaves = 0, 
  totalPayroll = 0 
}) {
  const cards = [
    {
      title: "Total Headcount",
      value: totalEmployees,
      badge: "100% Onboarded",
      badgeColor: "text-emerald-700 bg-emerald-50 border-emerald-200",
      icon: Users,
      iconColor: "text-indigo-600",
      iconBg: "bg-indigo-50",
    },
    {
      title: "Present Today",
      value: presentToday,
      badge: `${totalEmployees > 0 ? Math.round((presentToday / totalEmployees) * 100) : 0}% Turnout`,
      badgeColor: "text-emerald-700 bg-emerald-50 border-emerald-200",
      icon: CalendarCheck2,
      iconColor: "text-emerald-600",
      iconBg: "bg-emerald-50",
    },
    {
      title: "Pending Leaves",
      value: pendingLeaves,
      badge: pendingLeaves > 0 ? "Action Required" : "All Clear",
      badgeColor: pendingLeaves > 0 
        ? "text-amber-700 bg-amber-50 border-amber-200" 
        : "text-slate-600 bg-slate-100 border-slate-200",
      icon: Clock,
      iconColor: "text-amber-600",
      iconBg: "bg-amber-50",
    },
    {
      title: "Monthly Payroll Est.",
      value: `$${(totalPayroll / 12).toLocaleString(undefined, { maximumFractionDigits: 0 })}`,
      badge: "Active CTC",
      badgeColor: "text-slate-600 bg-slate-100 border-slate-200",
      icon: DollarSign,
      iconColor: "text-violet-600",
      iconBg: "bg-violet-50",
    },
  ];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {cards.map((card, idx) => {
        const IconComponent = card.icon;
        return (
          <div
            key={idx}
            className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow duration-200 flex items-center justify-between"
          >
            <div className="space-y-1">
              <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                {card.title}
              </p>
              <h3 className="text-2xl font-black text-slate-900 tracking-tight">
                {card.value}
              </h3>
              <div className="pt-1">
                <span className={`inline-flex items-center gap-1 text-[11px] font-semibold px-2 py-0.5 rounded-md border ${card.badgeColor}`}>
                  {card.badge}
                  <ArrowUpRight className="w-3 h-3" />
                </span>
              </div>
            </div>

            <div className={`p-3.5 rounded-2xl ${card.iconBg} ${card.iconColor}`}>
              <IconComponent className="w-6 h-6" />
            </div>
          </div>
        );
      })}
    </div>
  );
}
