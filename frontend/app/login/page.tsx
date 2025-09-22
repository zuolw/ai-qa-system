import AuthForm from '../../components/AuthForm';
import Link from 'next/link';

export default function LoginPage() {
  return (
    <div className="min-h-[calc(100vh-64px)] flex items-center justify-center px-4">
      <div className="glass-card p-8 rounded-3xl w-full max-w-md">
        <div className="text-center mb-6">
          <h2 className="text-2xl font-bold text-white">登录您的账户</h2>
          <p className="text-white/60 text-sm mt-1">继续与 AI 助手畅聊</p>
        </div>
        <AuthForm type="login" />
        <p className="text-center text-white/60 mt-6 text-sm">
          还没有账户？{' '}
          <Link href="/register" className="text-cyan-300 hover:text-cyan-200">
            立即注册
          </Link>
        </p>
      </div>
    </div>
  );
}