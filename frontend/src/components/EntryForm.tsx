import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { format } from 'date-fns';

interface EntryFormProps {
  onClose: () => void;
  initialType?: 'INCOME' | 'EXPENSE';
}

export const EntryForm: React.FC<EntryFormProps> = ({ onClose, initialType = 'EXPENSE' }) => {
  const { categories, addEntry } = useApp();
  const [formData, setFormData] = useState({
    entryType: initialType,
    categoryId: 0,
    amount: '',
    date: format(new Date(), 'yyyy-MM-dd'),
    description: '',
    memo: '',
  });

  const filteredCategories = categories.filter(cat => cat.type === formData.entryType);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.categoryId || !formData.amount || !formData.description) {
      alert('필수 항목을 모두 입력해주세요.');
      return;
    }

    const category = categories.find(c => c.id === Number(formData.categoryId));
    
    addEntry({
      entryType: formData.entryType,
      categoryId: Number(formData.categoryId),
      categoryName: category?.name,
      amount: Number(formData.amount),
      date: formData.date,
      description: formData.description,
      memo: formData.memo,
    });

    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <h2 className="text-xl font-bold mb-4">
          {formData.entryType === 'INCOME' ? '수입' : '지출'} 추가
        </h2>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              구분
            </label>
            <div className="flex gap-2">
              <button
                type="button"
                className={`flex-1 py-2 px-4 rounded-md ${
                  formData.entryType === 'INCOME'
                    ? 'bg-green-500 text-white'
                    : 'bg-gray-200 text-gray-700'
                }`}
                onClick={() => setFormData({ ...formData, entryType: 'INCOME', categoryId: 0 })}
              >
                수입
              </button>
              <button
                type="button"
                className={`flex-1 py-2 px-4 rounded-md ${
                  formData.entryType === 'EXPENSE'
                    ? 'bg-red-500 text-white'
                    : 'bg-gray-200 text-gray-700'
                }`}
                onClick={() => setFormData({ ...formData, entryType: 'EXPENSE', categoryId: 0 })}
              >
                지출
              </button>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              카테고리 *
            </label>
            <select
              value={formData.categoryId}
              onChange={(e) => setFormData({ ...formData, categoryId: Number(e.target.value) })}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            >
              <option value="0">선택하세요</option>
              {filteredCategories.map(category => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              금액 *
            </label>
            <input
              type="number"
              value={formData.amount}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="0"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              날짜 *
            </label>
            <input
              type="date"
              value={formData.date}
              onChange={(e) => setFormData({ ...formData, date: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              내용 *
            </label>
            <input
              type="text"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="예: 점심 식사"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              메모
            </label>
            <textarea
              value={formData.memo}
              onChange={(e) => setFormData({ ...formData, memo: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              rows={3}
              placeholder="추가 메모 (선택사항)"
            />
          </div>

          <div className="flex gap-2 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2 px-4 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
            >
              취소
            </button>
            <button
              type="submit"
              className="flex-1 py-2 px-4 bg-blue-500 text-white rounded-md hover:bg-blue-600"
            >
              저장
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};