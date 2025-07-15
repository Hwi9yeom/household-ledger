import React, { useState, useMemo } from 'react';
import { useApp } from '../context/AppContext';
import { EntryForm } from './EntryForm';
import { format, startOfMonth, endOfMonth, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';
import { PlusIcon, TrashIcon, ArrowUpIcon, ArrowDownIcon } from '@heroicons/react/24/outline';

export const Dashboard: React.FC = () => {
  const { entries, categories, deleteEntry, user } = useApp();
  const [showEntryForm, setShowEntryForm] = useState(false);
  const [selectedMonth, setSelectedMonth] = useState(format(new Date(), 'yyyy-MM'));
  const [entryType, setEntryType] = useState<'INCOME' | 'EXPENSE'>('EXPENSE');

  // Filter entries by selected month
  const monthlyEntries = useMemo(() => {
    const monthStart = startOfMonth(new Date(selectedMonth));
    const monthEnd = endOfMonth(new Date(selectedMonth));
    
    return entries.filter(entry => {
      const entryDate = parseISO(entry.date);
      return entryDate >= monthStart && entryDate <= monthEnd;
    });
  }, [entries, selectedMonth]);

  // Calculate totals
  const totals = useMemo(() => {
    const income = monthlyEntries
      .filter(e => e.entryType === 'INCOME')
      .reduce((sum, e) => sum + e.amount, 0);
    
    const expense = monthlyEntries
      .filter(e => e.entryType === 'EXPENSE')
      .reduce((sum, e) => sum + e.amount, 0);
    
    return { income, expense, balance: income - expense };
  }, [monthlyEntries]);

  // Group entries by date
  const groupedEntries = useMemo(() => {
    const grouped = monthlyEntries.reduce((acc, entry) => {
      const date = entry.date;
      if (!acc[date]) {
        acc[date] = [];
      }
      acc[date].push(entry);
      return acc;
    }, {} as Record<string, typeof monthlyEntries>);
    
    // Sort dates in descending order
    return Object.entries(grouped)
      .sort(([a], [b]) => b.localeCompare(a))
      .map(([date, entries]) => ({
        date,
        entries: entries.sort((a, b) => b.createdAt!.localeCompare(a.createdAt!))
      }));
  }, [monthlyEntries]);

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('ko-KR', {
      style: 'currency',
      currency: 'KRW',
      maximumFractionDigits: 0,
    }).format(amount);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto p-4">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <div className="flex justify-between items-center mb-4">
            <h1 className="text-2xl font-bold text-gray-800">가계부</h1>
            <div className="text-sm text-gray-600">
              {user ? (
                <span>👤 {user.name}</span>
              ) : (
                <span>💾 로컬 저장 모드</span>
              )}
            </div>
          </div>
          
          {/* Month selector */}
          <div className="flex items-center justify-center mb-4">
            <input
              type="month"
              value={selectedMonth}
              onChange={(e) => setSelectedMonth(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Summary */}
          <div className="grid grid-cols-3 gap-4">
            <div className="text-center">
              <p className="text-sm text-gray-600">수입</p>
              <p className="text-xl font-bold text-green-600">
                {formatCurrency(totals.income)}
              </p>
            </div>
            <div className="text-center">
              <p className="text-sm text-gray-600">지출</p>
              <p className="text-xl font-bold text-red-600">
                {formatCurrency(totals.expense)}
              </p>
            </div>
            <div className="text-center">
              <p className="text-sm text-gray-600">잔액</p>
              <p className={`text-xl font-bold ${totals.balance >= 0 ? 'text-blue-600' : 'text-red-600'}`}>
                {formatCurrency(totals.balance)}
              </p>
            </div>
          </div>
        </div>

        {/* Add buttons */}
        <div className="flex gap-2 mb-6">
          <button
            onClick={() => {
              setEntryType('INCOME');
              setShowEntryForm(true);
            }}
            className="flex-1 flex items-center justify-center gap-2 py-3 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors"
          >
            <ArrowUpIcon className="w-5 h-5" />
            수입 추가
          </button>
          <button
            onClick={() => {
              setEntryType('EXPENSE');
              setShowEntryForm(true);
            }}
            className="flex-1 flex items-center justify-center gap-2 py-3 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
          >
            <ArrowDownIcon className="w-5 h-5" />
            지출 추가
          </button>
        </div>

        {/* Entries list */}
        <div className="space-y-4">
          {groupedEntries.length === 0 ? (
            <div className="bg-white rounded-lg shadow-sm p-8 text-center text-gray-500">
              이번 달 내역이 없습니다.
            </div>
          ) : (
            groupedEntries.map(({ date, entries }) => (
              <div key={date} className="bg-white rounded-lg shadow-sm p-4">
                <h3 className="text-sm font-medium text-gray-600 mb-3">
                  {format(parseISO(date), 'M월 d일 (EEEE)', { locale: ko })}
                </h3>
                <div className="space-y-2">
                  {entries.map((entry) => {
                    const category = categories.find(c => c.id === entry.categoryId);
                    return (
                      <div
                        key={entry.id}
                        className="flex items-center justify-between py-2 border-b border-gray-100 last:border-0"
                      >
                        <div className="flex-1">
                          <div className="flex items-center gap-2">
                            <span
                              className="w-2 h-2 rounded-full"
                              style={{ backgroundColor: category?.color || '#6B7280' }}
                            />
                            <span className="text-sm text-gray-600">{category?.name}</span>
                            <span className="text-gray-800">{entry.description}</span>
                          </div>
                          {entry.memo && (
                            <p className="text-xs text-gray-500 mt-1 ml-4">{entry.memo}</p>
                          )}
                        </div>
                        <div className="flex items-center gap-3">
                          <span className={`font-medium ${
                            entry.entryType === 'INCOME' ? 'text-green-600' : 'text-red-600'
                          }`}>
                            {entry.entryType === 'INCOME' ? '+' : '-'}
                            {formatCurrency(entry.amount)}
                          </span>
                          <button
                            onClick={() => deleteEntry(entry.id)}
                            className="text-gray-400 hover:text-red-500 transition-colors"
                          >
                            <TrashIcon className="w-4 h-4" />
                          </button>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      {/* Entry form modal */}
      {showEntryForm && (
        <EntryForm
          onClose={() => setShowEntryForm(false)}
          initialType={entryType}
        />
      )}
    </div>
  );
};