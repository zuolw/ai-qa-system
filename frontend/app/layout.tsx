import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'AI聊天应用 - Next.js全栈演示',
  description: '基于Next.js + BFF架构 + Gemini AI的聊天应用',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="zh-CN">
      <body className={`${inter.className} tech-bg`}>
        <div className="fixed inset-0 pointer-events-none">
          <div className="absolute inset-x-0 top-0 h-24 bg-gradient-to-b from-white/10 to-transparent"></div>
        </div>
        <header className="sticky top-0 z-50">
          <nav className="mx-auto max-w-7xl px-4 py-4 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="h-9 w-9 rounded-xl bg-gradient-to-tr from-cyan-400 to-fuchsia-500 shadow-lg"></div>
              <span className="text-white/90 font-semibold tracking-wide">AI QA System</span>
            </div>
            <div className="hidden sm:flex items-center gap-6 text-sm">
              <a href="/" className="text-white/70 hover:text-white transition">首页</a>
              <a href="/chat" className="text-white/70 hover:text-white transition">聊天</a>
              <a href="/login" className="text-white/70 hover:text-white transition">登录</a>
              <a href="/register" className="inline-flex items-center rounded-lg bg-white/10 text-white hover:bg-white/20 px-3 py-1.5 transition">注册</a>
            </div>
          </nav>
        </header>
        <main className="relative z-10">{children}</main>
        <footer className="mt-20 border-t border-white/10">
          <div className="mx-auto max-w-7xl px-4 py-8 text-xs text-white/50 flex items-center justify-between">
            <span>© {new Date().getFullYear()} AI QA System</span>
            <span>Next.js · TailwindCSS · Spring Cloud</span>
          </div>
        </footer>
      </body>
    </html>
  );
}