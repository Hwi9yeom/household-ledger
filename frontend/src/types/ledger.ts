export interface LedgerEntry {
  id: string;
  userId?: number;
  entryType: 'INCOME' | 'EXPENSE';
  transactionType?: string;
  categoryId?: number;
  categoryName?: string;
  amount: number;
  date: string;
  description: string;
  memo?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Category {
  id: number;
  name: string;
  type: 'INCOME' | 'EXPENSE' | 'SAVINGS';
  subType?: string;
  icon?: string;
  color?: string;
}

export interface MonthlyExpense {
  userId: number;
  year: number;
  month: number;
  totalExpense: number;
  categoryExpenses: CategoryExpense[];
}

export interface CategoryExpense {
  categoryId: number;
  categoryName: string;
  amount: number;
  percentage: number;
}

export interface User {
  id: number;
  email: string;
  name: string;
  isAuthenticated: boolean;
}