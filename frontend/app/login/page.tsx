import AuthForm from '../../components/AuthForm';
import Link from 'next/link';

export default function LoginPage() {
  return (
    <div className="min-h-screen flex items-center justify-center gradient-bg">
      <div className="bg-white p-8 rounded-2xl shadow-2xl w-full max-w-md">
        <h2 className="text-2xl font-bold mb-6 text-gray-800 text-center">登录您的账户</h2>
        <AuthForm type="login" />
        <p className="text-center text-gray-600 mt-6">
          还没有账户？{' '}
          <Link href="/register" className="text-blue-600 font-medium hover:underline">
            立即注册
          </Link>
        </p>
      </div>
    </div>
  );
}