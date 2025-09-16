// app/page.tsx
import Link from 'next/link';

export default function Home() {
  return (
    <div className="min-h-screen gradient-bg flex flex-col items-center justify-center p-8">
      <div className="text-center text-white mb-12">
        <h1 className="text-5xl font-bold mb-6">AI聊天应用</h1>
        <p className="text-xl opacity-90">基于Next.js + BFF架构 + Gemini AI</p>
      </div>
      
      <div className="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-md">
        <h2 className="text-2xl font-bold mb-6 text-gray-800 text-center">欢迎使用</h2>
        
        <div className="space-y-4">
          <Link
            href="/login"
            className="block w-full bg-blue-600 text-white py-3 rounded-lg font-semibold text-center hover:bg-blue-700 transition duration-200"
          >
            登录
          </Link>
          
          <Link
            href="/register"
            className="block w-full bg-green-600 text-white py-3 rounded-lg font-semibold text-center hover:bg-green-700 transition duration-200"
          >
            注册新账户
          </Link>
          
          <Link
            href="/chat"
            className="block w-full bg-purple-600 text-white py-3 rounded-lg font-semibold text-center hover:bg-purple-700 transition duration-200"
          >
            体验AI聊天
          </Link>
        </div>
        
      </div>
    </div>
  );
}