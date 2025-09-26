'use client';

import { useState } from 'react';

interface Message {
  role: 'user' | 'assistant';
  content: string;
}

export default function ChatInterface() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage: Message = { role: 'user', content: input };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setIsLoading(true);

    try {
      const response = await fetch('/api/chat', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          messages: [...messages, userMessage],
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      const answer = data?.answer || data?.error || '抱歉，暂时无法回答。';
      const assistantMessage: Message = { role: 'assistant', content: answer };
      setMessages(prev => [...prev, assistantMessage]);
    } catch (error) {
      console.error('Error sending message:', error);
      const errorMessage: Message = {
        role: 'assistant',
        content: '抱歉，暂时无法连接到服务器，请稍后再试。'
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col h-full max-h-[80vh]">
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
        {isLoading && (
          <div className="flex justify-start">
            <div className="bg-white/10 text-white border border-white/10 p-4 rounded-2xl">
              <div className="flex items-center space-x-2">
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                <span>AI正在思考...</span>
              </div>
            </div>
          </div>
        )}
      </div>

      <div className="border-t border-white/10 p-4">
        <form onSubmit={handleSubmit} className="flex gap-2">
          <div className="flex-1 relative">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="输入您的问题..."
              disabled={isLoading}
              className="w-full px-4 py-3 rounded-xl bg-white text-gray-900 placeholder-gray-500 border border-white/20 focus:outline-none focus:ring-2 focus:ring-cyan-400/50 disabled:opacity-50"
            />
          </div>
          <button
            type="submit"
            disabled={isLoading || !input.trim()}
            className="px-5 py-3 rounded-xl bg-gradient-to-r from-cyan-400 to-fuchsia-500 text-white font-semibold shadow-lg shadow-fuchsia-500/20 hover:opacity-90 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isLoading ? '发送中...' : '发送'}
          </button>
        </form>
      </div>
    </div>
  );
}
