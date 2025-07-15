import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { LedgerEntry, Category, User } from '../types/ledger';
import { localStorage } from '../utils/storage';

interface AppContextType {
  entries: LedgerEntry[];
  categories: Category[];
  user: User | null;
  isAuthenticated: boolean;
  addEntry: (entry: Omit<LedgerEntry, 'id' | 'createdAt' | 'updatedAt'>) => void;
  updateEntry: (id: string, entry: Partial<LedgerEntry>) => void;
  deleteEntry: (id: string) => void;
  login: (user: User) => void;
  logout: () => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within AppProvider');
  }
  return context;
};

interface AppProviderProps {
  children: ReactNode;
}

export const AppProvider: React.FC<AppProviderProps> = ({ children }) => {
  const [entries, setEntries] = useState<LedgerEntry[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    // Load data from localStorage on mount
    setEntries(localStorage.getEntries());
    setCategories(localStorage.getCategories());
    const savedUser = localStorage.getUser();
    if (savedUser) {
      setUser(savedUser);
    }
  }, []);

  const addEntry = (entryData: Omit<LedgerEntry, 'id' | 'createdAt' | 'updatedAt'>) => {
    const newEntry: LedgerEntry = {
      ...entryData,
      id: Date.now().toString(),
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };

    const updatedEntries = [...entries, newEntry];
    setEntries(updatedEntries);
    
    // Save to localStorage for non-authenticated users
    if (!user?.isAuthenticated) {
      localStorage.saveEntries(updatedEntries);
    }
    // TODO: For authenticated users, save to backend
  };

  const updateEntry = (id: string, entryData: Partial<LedgerEntry>) => {
    const updatedEntries = entries.map(entry =>
      entry.id === id
        ? { ...entry, ...entryData, updatedAt: new Date().toISOString() }
        : entry
    );
    setEntries(updatedEntries);
    
    if (!user?.isAuthenticated) {
      localStorage.saveEntries(updatedEntries);
    }
    // TODO: For authenticated users, update in backend
  };

  const deleteEntry = (id: string) => {
    const updatedEntries = entries.filter(entry => entry.id !== id);
    setEntries(updatedEntries);
    
    if (!user?.isAuthenticated) {
      localStorage.saveEntries(updatedEntries);
    }
    // TODO: For authenticated users, delete from backend
  };

  const login = (userData: User) => {
    setUser(userData);
    localStorage.saveUser(userData);
    // TODO: Load user data from backend
  };

  const logout = () => {
    setUser(null);
    localStorage.clearUser();
    // Clear entries if user wants to start fresh
    setEntries([]);
    localStorage.clearAll();
  };

  return (
    <AppContext.Provider
      value={{
        entries,
        categories,
        user,
        isAuthenticated: !!user?.isAuthenticated,
        addEntry,
        updateEntry,
        deleteEntry,
        login,
        logout,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};