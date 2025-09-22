'use client';

import { useChat } from 'ai/react';

export default function ChatInterface() {
  const { messages, input, handleInputChange, handleSubmit } = useChat();

  return (
    <div className="flex flex-col min-h-[60vh] h-full">
      <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-white/5 rounded-2xl border border-white/10">
        {messages.length === 0 ? (
          <div className="text-center text-white/60 mt-10">
            <p>您好！我是AI助手，有什么我可以帮您的吗？</p>
          </div>
        ) : (
          messages.map((message, index) => (
            <div
              key={index}
              className={`message-slide-in flex ${
                message.role === 'user' ? 'justify-end' : 'justify-start'
              }`}
            >
              <div
                className={`max-w-[80%] p-4 rounded-2xl ${
                  message.role === 'user'
                    ? 'bg-gradient-to-r from-cyan-500/20 to-fuchsia-500/20 text-white border border-white/20'
                    : 'bg-white/10 text-white border border-white/10'
                }`}
              >
                <p>{message.content}</p>
              </div>
            </div>
          ))
        )}
      </div>
      
      <form onSubmit={handleSubmit} className="mt-4 flex gap-2">
        <div className="flex-1 relative">
          <input
            type="text"
            value={input}
            onChange={handleInputChange}
            placeholder="输入您的问题..."
            className="w-full px-4 py-3 rounded-xl bg-white text-gray-900 placeholder-gray-500 border border-white/20 focus:outline-none focus:ring-2 focus:ring-cyan-400/50"
          />
        </div>
        <button
          type="submit"
          className="px-5 py-3 rounded-xl bg-gradient-to-r from-cyan-400 to-fuchsia-500 text-white font-semibold shadow-lg shadow-fuchsia-500/20 hover:opacity-90 transition"
        >
          发送
        </button>
      </form>
    </div>
  );
}
