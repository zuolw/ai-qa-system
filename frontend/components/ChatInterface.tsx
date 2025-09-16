'use client';

import { useChat } from 'ai/react';

export default function ChatInterface() {
  const { messages, input, handleInputChange, handleSubmit } = useChat();
  
  return (
    <div className="flex flex-col h-full">
      <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-100 rounded-lg mb-4">
        {messages.length === 0 ? (
          <div className="text-center text-gray-500 mt-10">
            <p>您好！我是AI助手，有什么我可以帮您的吗？</p>
          </div>
        ) : (
          messages.map((message, index) => (
            <div
              key={index}
              className={`flex ${
                message.role === 'user' ? 'justify-end' : 'justify-start'
              }`}
            >
              <div
                className={`max-w-xs p-4 rounded-2xl ${
                  message.role === 'user'
                    ? 'bg-green-100 text-green-900'
                    : 'bg-blue-100 text-blue-900'
                }`}
              >
                <p>{message.content}</p>
              </div>
            </div>
          ))
        )}
      </div>
      
      <form onSubmit={handleSubmit} className="flex gap-2">
        <input
          type="text"
          value={input}
          onChange={handleInputChange}
          placeholder="输入您的问题..."
          className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          type="submit"
          className="bg-blue-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-blue-700 transition duration-200"
        >
          发送
        </button>
      </form>
    </div>
  );
}
