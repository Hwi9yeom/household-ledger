import { LedgerEntry, Category } from '../types/ledger';

const STORAGE_KEYS = {
  ENTRIES: 'ledger_entries',
  CATEGORIES: 'ledger_categories',
  USER: 'ledger_user',
};

export const localStorage = {
  // Entries
  getEntries: (): LedgerEntry[] => {
    const data = window.localStorage.getItem(STORAGE_KEYS.ENTRIES);
    return data ? JSON.parse(data) : [];
  },

  saveEntries: (entries: LedgerEntry[]) => {
    window.localStorage.setItem(STORAGE_KEYS.ENTRIES, JSON.stringify(entries));
  },

  addEntry: (entry: LedgerEntry) => {
    const entries = localStorage.getEntries();
    entries.push(entry);
    localStorage.saveEntries(entries);
  },

  updateEntry: (id: string, updatedEntry: Partial<LedgerEntry>) => {
    const entries = localStorage.getEntries();
    const index = entries.findIndex(e => e.id === id);
    if (index !== -1) {
      entries[index] = { ...entries[index], ...updatedEntry, updatedAt: new Date().toISOString() };
      localStorage.saveEntries(entries);
    }
  },

  deleteEntry: (id: string) => {
    const entries = localStorage.getEntries();
    const filtered = entries.filter(e => e.id !== id);
    localStorage.saveEntries(filtered);
  },

  // Categories
  getCategories: (): Category[] => {
    const data = window.localStorage.getItem(STORAGE_KEYS.CATEGORIES);
    if (data) {
      return JSON.parse(data);
    }
    
    // Default categories
    const defaultCategories: Category[] = [
      // Income categories
      { id: 1, name: '급여', type: 'INCOME', color: '#10B981' },
      { id: 2, name: '부수입', type: 'INCOME', color: '#34D399' },
      { id: 3, name: '투자수익', type: 'INCOME', color: '#6EE7B7' },
      { id: 4, name: '기타수입', type: 'INCOME', color: '#A7F3D0' },
      
      // Expense categories
      { id: 5, name: '식비', type: 'EXPENSE', color: '#EF4444' },
      { id: 6, name: '교통비', type: 'EXPENSE', color: '#F87171' },
      { id: 7, name: '주거비', type: 'EXPENSE', color: '#FCA5A5' },
      { id: 8, name: '의료비', type: 'EXPENSE', color: '#FBBF24' },
      { id: 9, name: '교육비', type: 'EXPENSE', color: '#FCD34D' },
      { id: 10, name: '통신비', type: 'EXPENSE', color: '#FDE68A' },
      { id: 11, name: '생활용품', type: 'EXPENSE', color: '#A78BFA' },
      { id: 12, name: '문화생활', type: 'EXPENSE', color: '#C4B5FD' },
      { id: 13, name: '기타지출', type: 'EXPENSE', color: '#DDD6FE' },
      
      // Savings categories
      { id: 14, name: '저축', type: 'SAVINGS', color: '#3B82F6' },
      { id: 15, name: '투자', type: 'SAVINGS', color: '#60A5FA' },
    ];
    
    localStorage.saveCategories(defaultCategories);
    return defaultCategories;
  },

  saveCategories: (categories: Category[]) => {
    window.localStorage.setItem(STORAGE_KEYS.CATEGORIES, JSON.stringify(categories));
  },

  // User
  getUser: () => {
    const data = window.localStorage.getItem(STORAGE_KEYS.USER);
    return data ? JSON.parse(data) : null;
  },

  saveUser: (user: any) => {
    window.localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user));
  },

  clearUser: () => {
    window.localStorage.removeItem(STORAGE_KEYS.USER);
  },

  // Clear all data
  clearAll: () => {
    window.localStorage.removeItem(STORAGE_KEYS.ENTRIES);
    window.localStorage.removeItem(STORAGE_KEYS.CATEGORIES);
    window.localStorage.removeItem(STORAGE_KEYS.USER);
  }
};